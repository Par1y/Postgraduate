package cn.sicnu.postgraduate.core.service

import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
import cn.sicnu.postgraduate.core.mapper.PlanMapper
import cn.sicnu.postgraduate.core.entity.Plan
import cn.sicnu.postgraduate.core.exception.CustomException
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

/*
    计划服务

    查询计划
    getPlan: Plan，成功包装对象，失败空对象&错误信息
    getPlanBy: List<Plan>，成功包装对象，失败空对象&错误信息

    新建计划
    newPlan: Plan，成功包装对象，失败空对象&错误信息

    修改计划
    alterPlan: Plan，成功包装对象，失败空对象&错误信息

    删除计划
    deletePlan: Plan，成功包装对象，失败空对象&错误信息
 */
@Service
class PlanServiceImpl(
    private val planMapper: PlanMapper,
    private val cacheManager: CacheManager
): PlanService {
    companion object {
        // 日志模块
        private val logger: Logger = LoggerFactory.getLogger(PlanService::class.java)

        // 常量声明
        private const val CODE_MISSING: Int = -1
        private const val MESSAGE_MISSING: String = "计划不存在"

        private const val CODE_UNCHANGED: Int = -3
        private const val MESSAGE_UNCHANGED: String = "未改动"
        private const val CODE_DATABASE_ERROR: Int = -4
        private const val MESSAGE_DATABASE_ERROR: String = "数据库错误"
        private const val CODE_WRONG_TIME: Int = -5
        private const val MESSAGE_WRONG_TIME: String = "时间参数错误"

        private const val CODE_PERMISSION_DENIED: Int = -2
        private const val MESSAGE_PERMISSION_DENIED: String = "无权限"
    }


    /*  查询计划
        pidStr      计划id
     */
    @Cacheable(value = ["plan"], key = "#pidStr")
    override fun getPlan(pidStr: String): Plan {
        //转换参数
        val pid: Long = Convert.toLong(pidStr)

        val plan: Plan? = planMapper.selectById(pid)
        if (plan != null) {
            return plan
        } else {
            // 无此计划
            logger.info("getPlan: pid {}, {}", pid, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    /*  批量查询计划
        uidStr      用户id
        lBeginDateStr       起始时间
        lEndDateStr     结束时间
     */
    @Cacheable(value = ["plan"], key = "#pidStr")
    override fun getPlanBy(uidStr: String, lBeginDateStr: String?, lEndDateStr: String?): List<Plan> {
        //转换参数
        val uid: Long = Convert.toLong(uidStr)
        val lBeginDate: Long = Convert.toLong(lBeginDateStr)
        val lEndDate: Long = Convert.toLong(lEndDateStr)

        val startOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        //转换时间
        var beginDate: LocalDateTime? = null
        var endDate: LocalDateTime? = null
        if(lBeginDate != null){ beginDate  = lBeginDate?.let { timestampConvert(it) } }
        if(lBeginDate != null){ endDate  = lEndDate?.let { timestampConvert(it) } }

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
                throw CustomException(CODE_WRONG_TIME, MESSAGE_WRONG_TIME)
            }
        }

        val plans: List<Plan> = planMapper.selectList(queryWrapper)
        return plans
    }

    /*  新建计划
        uidStr      用户id
        lDateStr        创建时间（timestamp milliseconds）
        content     内容
     */
    @CachePut(value = ["plan"], key = "T(java.lang.String).valueOf(#result.pid)")
    override fun newPlan(uidStr: String, lDateStr: String, content: String): Plan {
        //转换参数
        val uid: Long = Convert.toLong(uidStr)
        val lDate: Long = Convert.toLong(lDateStr)

        //鉴权
        val user: User = getUser()
        if(user.getUid() != uid) {
            //鉴权失败，试图冒充他人
            logger.error("newPlan: forwardUid: {},realUid: {}, {}", uid, user.getUid(), MESSAGE_PERMISSION_DENIED)
            throw CustomException(CODE_PERMISSION_DENIED, MESSAGE_PERMISSION_DENIED)
        }

        //转换时间
        var date: LocalDateTime = timestampConvert(lDate)

        //生成新计划
        val snowFlake: Snowflake = IdUtil.getSnowflake()
        val pid: Long = snowFlake.nextId()
        val newPlan = Plan().apply {
            setPid(pid)
            setUid(uid)
            setDate(date)
            setContent(content)
        }

        //新建计划
        val result: Int = planMapper.insert(newPlan)
        if (result == 1) {
            return newPlan
        } else if (result < 1) {
            // 插入失败
            logger.error("newPlan: uid {}, date {}, {} 插入失败", uid, date, MESSAGE_DATABASE_ERROR)
            throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        } else {
            // 意外情况
            logger.error("newPlan: uid {}, date {}, {} 插入行数大于1", uid, date, MESSAGE_DATABASE_ERROR)
            throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }
    }

    /*  修改计划
        pidStr      计划id
        lDateStr        创建时间（timestamp milliseconds）
        content     内容
     */
    @CachePut(value = ["plan"], key = "#pidStr")
    override fun alterPlan(pidStr: String, lDateStr: String?, content: String?): Plan {
        //参数转换
        val pid: Long = Convert.toLong(pidStr)
        val lDate: Long = Convert.toLong(lDateStr)

        //转换时间
        var date: LocalDateTime? = null
        if(lDate != null){ date = timestampConvert(lDate) }

        //未修改
        if (date == null && content == null) {
            logger.info("alterPlan: pid {}, {}", pid, MESSAGE_UNCHANGED)
            throw CustomException(CODE_UNCHANGED, MESSAGE_UNCHANGED)
        }

        //修改计划
        val updatePlan: Plan? = planMapper.selectById(pid)

        if (updatePlan != null) {
            //鉴权
            val user: User = getUser()
            if(user.getUid() != updatePlan.getUid()) {
                //鉴权失败，试图修改他人计划
                logger.error("alterPlan: pid {},uid: {}, {}", pid, user.getUid(), MESSAGE_PERMISSION_DENIED)
                throw CustomException(CODE_PERMISSION_DENIED, MESSAGE_PERMISSION_DENIED)
            }

            // 比对修改内容
            date?.let { updatePlan.setDate(it) }
            content?.let { updatePlan.setContent(it) }

            // 修改
            val result = planMapper.updateById(updatePlan)
            if (result == 1) {
                return updatePlan
            } else if (result < 1) {
                // 修改失败
                logger.error("alterPlan: pid {}, {} 修改失败", pid, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            } else {
                // 意外情况
                logger.error("alterPlan: pid {}, {} 修改行数大于1", pid, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        } else {
            // 无此计划
            logger.info("alterPlan: pid {}, {}", pid, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    /*  删除计划
        pidStr      计划id
     */
    @CacheEvict(value = ["plan"], key = "#pidStr")
    override fun deletePlan(pidStr: String): Plan {
        //参数转换
        val pid: Long = Convert.toLong(pidStr)

        //删除计划
        val dPlan: Plan? = planMapper.selectById(pid)
        if (dPlan != null) {
            //鉴权
            val user: User = getUser()
            if(user.getUid() != dPlan.getUid()) {
                //鉴权失败，试图删除他人计划
                logger.error("deletePlan: pid {},uid: {}, {}", pid, user.getUid(), MESSAGE_PERMISSION_DENIED)
                throw CustomException(CODE_PERMISSION_DENIED, MESSAGE_PERMISSION_DENIED)
            }

            //删除
            val result: Int = planMapper.deleteById(pid)
            if (result == 1) {
                return dPlan
            } else if (result < 1) {
                logger.info("deletePlan: pid {}, {} 删除失败", pid, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            } else {
                // 意外情况
                logger.error("deletePlan: pid {}, {} 删除行数大于1", pid, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        } else {
            // 无此计划
            logger.info("deletePlan: pid {}, {}", pid, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    private fun timestampConvert(lDate: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lDate), ZoneOffset.UTC)
    }

    private fun getUser(): User{
        val auth: UsernamePasswordAuthenticationToken = SecurityContextHolder.getContext().getAuthentication() as UsernamePasswordAuthenticationToken
        val loginUser: LoginUser = auth.getPrincipal() as LoginUser
        val user: User = loginUser.getUser() ?: User()
        return user
    }
}
