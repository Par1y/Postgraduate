package cn.sicnu.postgraduate.core.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import cn.sicnu.postgraduate.core.mapper.PlanMapper
import cn.sicnu.postgraduate.core.entity.Plan
import cn.sicnu.postgraduate.core.entity.CommonResult
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable

/*
    计划服务

    查询计划
    getPlan: CommonResult<Plan>，成功包装对象，失败空对象&错误信息
    getPlanBy: CommonResult<List<Plan>>，成功包装对象，失败空对象&错误信息

    新建计划
    newPlan: CommonResult<Plan>，成功包装对象，失败空对象&错误信息

    修改计划
    alterPlan: CommonResult<Plan>，成功包装对象，失败空对象&错误信息

    删除计划
    deletePlan: CommonResult<Plan>，成功包装对象，失败空对象&错误信息
 */
@Service
class PlanServiceImpl(private val planMapper: PlanMapper): PlanService {

    companion object {
        // 日志模块
        private val logger: Logger = LoggerFactory.getLogger(PlanService::class.java)

        // 常量声明
        private const val CODE_SUCCESS: Int = 0
        private const val CODE_MISSING: Int = -1
        private const val MESSAGE_MISSING: String = "计划不存在"

        private const val CODE_UNCHANGED: Int = -3
        private const val MESSAGE_UNCHANGED: String = "未改动"
        private const val CODE_DATABASE_ERROR: Int = -4
        private const val MESSAGE_DATABASE_ERROR: String = "数据库错误"
        private const val CODE_WRONG_TIME: Int = -5
        private const val MESSAGE_WRONG_TIME: String = "时间参数错误"
    }

    @Cacheable(value = ["plan"], key = "#pid")
    override fun getPlan(pid: Long): CommonResult<Plan> {
        val plan: Plan? = planMapper.selectById(pid)
        return if (plan != null) {
            CommonResult.success(plan)
        } else {
            // 无此计划
            logger.info("getPlan: pid {}, {}", pid, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @Cacheable(value = ["plan"], key = "#pid")
    override fun getPlanBy(uid: Long, beginDate: LocalDateTime?, endDate: LocalDateTime?): CommonResult<List<Plan>> {
        val startOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        // 条件构造器
        val queryWrapper = QueryWrapper<Plan>()
        queryWrapper.eq("uid", uid) // 添加 uid 条件

        when {
            beginDate == null && endDate == null -> {
                // 不指定时间默认当前一周
                queryWrapper.between("date", startOfWeek, startOfWeek.plusWeeks(1))
            }
            beginDate != null && endDate != null && beginDate.isBefore(endDate) -> {
                // 有指定首尾时间
                queryWrapper.between("date", beginDate, endDate)
            }
            beginDate == null && endDate != null -> {
                // 省略表示，代表结束时间前一周
                queryWrapper.between("date", endDate.minusWeeks(1), endDate)
            }
            beginDate != null && endDate == null -> {
                // 省略表示，代表开始时间后一周
                queryWrapper.between("date", beginDate, beginDate.plusWeeks(1))
            }
            else -> {
                // 无法理解
                logger.info("getPlanBy: uid {}, {} to {}, {}", uid, beginDate, endDate, MESSAGE_WRONG_TIME)
                return CommonResult.error(CODE_WRONG_TIME, MESSAGE_WRONG_TIME)
            }
        }

        val plans: List<Plan> = planMapper.selectList(queryWrapper)
        return CommonResult.success(plans)
    }

    @CachePut(value = ["plan"], key = "#pid")
    override fun newPlan(uid: Long, date: LocalDateTime, content: String): CommonResult<Plan> {
        val newPlan = Plan().apply {
            setUid(uid)
            setDate(date)
            setContent(content)
        }

        val result: Int = planMapper.insert(newPlan)
        return if (result == 1) {
            CommonResult.success(newPlan)
        } else if (result < 1) {
            // 插入失败
            logger.error("newPlan: uid {}, date {}, {} 插入失败", uid, date, MESSAGE_DATABASE_ERROR)
            CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        } else {
            // 意外情况
            logger.error("newPlan: uid {}, date {}, {} 插入行数大于1", uid, date, MESSAGE_DATABASE_ERROR)
            CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }
    }

    @CachePut(value = ["plan"], key = "#pid")
    override fun alterPlan(pid: Long, date: LocalDateTime?, content: String?): CommonResult<Plan> {
        if (date == null && content == null) {
            logger.info("alterPlan: pid {}, {}", pid, MESSAGE_UNCHANGED)
            return CommonResult.error(CODE_UNCHANGED, MESSAGE_UNCHANGED)
        }

        val updatePlan: Plan? = planMapper.selectById(pid)
        return if (updatePlan != null) {
            // 比对修改内容
            date?.let { updatePlan.setDate(it) }
            content?.let { updatePlan.setContent(it) }

            // 修改
            val result = planMapper.updateById(updatePlan)
            if (result == 1) {
                CommonResult.success(updatePlan)
            } else if (result < 1) {
                // 修改失败
                logger.error("alterPlan: pid {}, {} 修改失败", pid, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            } else {
                // 意外情况
                logger.error("alterPlan: pid {}, {} 修改行数大于1", pid, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        } else {
            // 无此计划
            logger.info("alterPlan: pid {}, {}", pid, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @CacheEvict(value = ["plan"], key = "#pid")
    override fun deletePlan(pid: Long): CommonResult<Plan> {
        val dPlan: Plan? = planMapper.selectById(pid)
        return if (dPlan != null) {
            val result: Int = planMapper.deleteById(pid)
            if (result == 1) {
                CommonResult.success(dPlan)
            } else if (result < 1) {
                logger.info("deletePlan: pid {}, {} 删除失败", pid, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            } else {
                // 意外情况
                logger.error("deletePlan: pid {}, {} 删除行数大于1", pid, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        } else {
            // 无此计划
            logger.info("deletePlan: pid {}, {}", pid, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }
}
