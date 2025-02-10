package cn.sicnu.postgraduate.core.service

import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import cn.sicnu.postgraduate.core.mapper.DynamicMapper
import cn.sicnu.postgraduate.core.entity.Dynamic
import cn.sicnu.postgraduate.core.exception.CustomException
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import java.time.Instant
import java.time.ZoneOffset

/*
    动态服务

    查询动态
    getDynamic: Dynamic，成功包装对象，失败空对象&错误信息
    getDynamicBy: List<Dynamic>，成功包装对象，失败空对象&错误信息

    新建动态
    newDynamic: Dynamic，成功包装对象，失败空对象&错误信息

    修改动态
    alterDynamic: Dynamic，成功包装对象，失败空对象&错误信息

    删除动态
    deleteDynamic: Dynamic，成功包装对象，失败空对象&错误信息
 */
@Service
class DynamicServiceImpl(
    private val dynamicMapper: DynamicMapper,
    private val cacheManager: CacheManager
): DynamicService {

    companion object {
        // 日志模块
        private val logger: Logger = LoggerFactory.getLogger(DynamicServiceImpl::class.java)

        // 常量声明
        private const val CODE_MISSING: Int = -1
        private const val MESSAGE_MISSING: String = "动态不存在"

        private const val CODE_DATABASE_ERROR: Int = -4
        private const val MESSAGE_DATABASE_ERROR: String = "数据库错误"
        private const val CODE_WRONG_TIME: Int = -5
        private const val MESSAGE_WRONG_TIME: String = "时间参数错误"
    }

    @Cacheable(value = ["dynamic"], key = "#did")
     override fun getDynamic(did: Long): Dynamic {
        val dynamic: Dynamic? = dynamicMapper.selectById(did)
        if (dynamic != null) {
            return dynamic
        } else {
            logger.info("getDynamic: did {}, {}", did, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @Cacheable(value = ["dynamic"], key = "#did")
    override fun getDynamicBy(uid: Long?, lBeginDate: Long?, lEndDate: Long?, replyId: Long?): List<Dynamic> {
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
        val queryWrapper = QueryWrapper<Dynamic>()
        if(uid != null) { queryWrapper.eq("uid", uid) }     // 添加 uid 条件
        if(replyId != null) { queryWrapper.eq("replyId", replyId) }     //添加回复id条件
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
                    logger.info("getDynamicBy: uid {}, {} to {}, {}", uid, beginDate, endDate, MESSAGE_WRONG_TIME)
                    throw CustomException(CODE_WRONG_TIME, MESSAGE_WRONG_TIME)
            }
        }
        val dynamics: List<Dynamic> = dynamicMapper.selectList(queryWrapper)
        return dynamics
    }

    @CachePut(value = ["dynamic"])
    override fun newDynamic(uid: Long, lDate: Long, content: String, replyId: Long?): Dynamic {
        //转换时间
        var date: LocalDateTime = timestampConvert(lDate)

        val snowFlake: Snowflake = IdUtil.getSnowflake()
        val did: Long = snowFlake.nextId()
        val newDynamic: Dynamic = Dynamic(did, uid, date, content)
        if(replyId != null) newDynamic.replyId = replyId    //有回复

        val result: Int = dynamicMapper.insert(newDynamic)
        if (result == 1) {
            val cacheKey = "dynamic:$did"
            val cache = cacheManager.getCache("dynamic")
            cache?.put(cacheKey, newDynamic)
            return newDynamic
        } else if (result < 1) {
            // 插入失败
            logger.error("newDynamic: uid {}, date {}, {} 插入失败", uid, date, MESSAGE_DATABASE_ERROR)
            throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        } else {
            // 意外情况
            logger.error("newDynamic: uid {}, date {}, {} 插入行数大于1", uid, date, MESSAGE_DATABASE_ERROR)
            throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }
    }

    @CacheEvict(value = ["dynamic"], key = "#did")
    override fun deleteDynamic(did: Long): Dynamic {
        val dDynamic: Dynamic? = dynamicMapper.selectById(did)
        if (dDynamic != null) {
            val result: Int = dynamicMapper.deleteById(did)
            if (result == 1) {
                return dDynamic
            } else if (result < 1) {
                logger.info("deleteDynamic: did {}, {} 删除失败", did, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            } else {
                // 意外情况
                logger.error("deleteDynamic: did {}, {} 删除行数大于1", did, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        } else {
            // 无此动态
            logger.info("deleteDynamic: did {}, {}", did, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    private fun timestampConvert(lDate: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lDate), ZoneOffset.UTC)
    }
}
