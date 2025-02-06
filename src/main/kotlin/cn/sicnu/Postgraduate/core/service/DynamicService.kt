package cn.sicnu.Postgraduate.core.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import cn.sicnu.Postgraduate.core.mapper.DynamicMapper
import cn.sicnu.Postgraduate.core.obj.Dynamic
import cn.sicnu.Postgraduate.core.obj.CommonResult

/*
    动态服务

    查询动态
    getDynamic: CommonResult<Dynamic>，成功包装对象，失败空对象&错误信息
    getDynamicBy: CommonResult<List<Dynamic>>，成功包装对象，失败空对象&错误信息

    新建动态
    newDynamic: CommonResult<Dynamic>，成功包装对象，失败空对象&错误信息

    修改动态
    alterDynamic: CommonResult<Dynamic>，成功包装对象，失败空对象&错误信息

    删除动态
    deleteDynamic: CommonResult<Dynamic>，成功包装对象，失败空对象&错误信息
 */
@Service
class DynamicService(private val dynamicMapper: DynamicMapper) {

    companion object {
        // 日志模块
        private val logger: Logger = LoggerFactory.getLogger(DynamicService::class.java)

        // 常量声明
        private const val CODE_SUCCESS: Int = 0
        private const val CODE_MISSING: Int = -1
        private const val MESSAGE_MISSING: String = "动态不存在"

        private const val CODE_UNCHANGED: Int = -3
        private const val MESSAGE_UNCHANGED: String = "未改动"
        private const val CODE_DATABASE_ERROR: Int = -4
        private const val MESSAGE_DATABASE_ERROR: String = "数据库错误"
        private const val CODE_WRONG_TIME: Int = -5
        private const val MESSAGE_WRONG_TIME: String = "时间参数错误"
    }

    fun getDynamic(did: Long): CommonResult<Dynamic> {
        val dynamic: Dynamic? = dynamicMapper.selectById(did)
        return if (dynamic != null) {
            CommonResult.success(dynamic)
        } else {
            logger.info("getDynamic: did {}, {}", did, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    fun getDynamicBy(uid: Long?, beginDate: LocalDateTime?, endDate: LocalDateTime?, replyId: Long?): CommonResult<List<Dynamic>> {
        val startOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        // 条件构造器
        val queryWrapper = QueryWrapper<Dynamic>()
        // TODO: 实现条件获取动态

        val dynamics: List<Dynamic> = dynamicMapper.selectList(queryWrapper)
        return CommonResult.success(dynamics)
    }

    fun newDynamic(uid: Long, date: LocalDateTime, content: String, replyId: Long?): CommonResult<Dynamic> {
        var newDynamic = Dynamic()
        // 是否回复
        newDynamic.replyId = replyId ?: -1L
        newDynamic.uid = uid
        newDynamic.date = date
        newDynamic.content = content

        val result: Int = dynamicMapper.insert(newDynamic)
        return if (result == 1) {
            CommonResult.success(newDynamic)
        } else if (result < 1) {
            // 插入失败
            logger.error("newDynamic: uid {}, date {}, {} 插入失败", uid, date, MESSAGE_DATABASE_ERROR)
            CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        } else {
            // 意外情况
            logger.error("newDynamic: uid {}, date {}, {} 插入行数大于1", uid, date, MESSAGE_DATABASE_ERROR)
            CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }
    }

    fun deleteDynamic(did: Long): CommonResult<Dynamic> {
        val dDynamic: Dynamic? = dynamicMapper.selectById(did)
        return if (dDynamic != null) {
            val result: Int = dynamicMapper.deleteById(did)
            if (result == 1) {
                CommonResult.success(dDynamic)
            } else if (result < 1) {
                logger.info("deleteDynamic: did {}, {} 删除失败", did, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            } else {
                // 意外情况
                logger.error("deleteDynamic: did {}, {} 删除行数大于1", did, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        } else {
            // 无此动态
            logger.info("deleteDynamic: did {}, {}", did, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }
}
