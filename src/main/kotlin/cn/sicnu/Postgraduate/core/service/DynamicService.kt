package cn.sicnu.Postgraduate.core.service

import java.time.LocalDateTime
import org.springframework.boot.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import cn.sicnu.Postgraduate.core.obj.Dynamic

/*
    动态服务

    查询动态
    getDynamic: CommResult<Dynamic>，成功包装对象，失败空对象&错误信息
    getDynamicBy: CommonResult<List<Dynamic>>，成功包装对象，失败空对象&错误信息

    新建动态
    newDynamic: CommonResult<Dynamic>，成功包装对象，失败空对象&错误信息

    修改动态
    alterDynamic: CommonResult<Dynamic>，成功包装对象，失败空对象&错误信息

    删除动态
    deleteDynamic: CommonResult<Dynamic>，成功包装对象，失败空对象&错误信息
 */
@Service
class DynamicService {
    companion object {
        //日志模块
        private final logger: Logger = LoggerFactory.getLogger(userService.class)

        //常量声明
        private final CODE_SUCCESS: Integer = Integer.valueOf(0)
        private final CODE_MISSING: Integer = Integer.valueOf(-1)
        private final val MESSAGE_MISSING: String = "动态不存在"

        private final CODE_UNCHANGED: Integer = Integer.valueOf(-3)
        private final val MESSAGE_UNCHANGED: String = "未改动"
        private final CODE_DATABASE_ERROR: Integer = Integer.valueOf(-4)
        private final val MESSAGE_DATABASE_ERROR: String = "数据库错误"
        private final CODE_WRONG_TIME: Integer = Integer.valueOf(-5)
        private final val MESSAGE_WRONG_TIME: String = "时间参数错误"
    }

    @Autowired
    private dynamicMapper: DynamicMapper

    fun getDynamic(did: Long): commonResult<Dynamic> {
        private var dynamic: Dynamic = dynamicMapper.selectById(did)
        if(dynamic != null) {
            //返回
            return CommonResult.success(dynamic)
        }else {
            //无此动态
            logger.info("getDynamic: did {}, {}", did, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MESSING)
        }
    }

    fun getDynamicBy(uid: Long?, beginDate: LocalDateTime?, endDate: LocalDateTime?, replyId: Long?): CommonResult<List<Dynamic>> {
        private val startOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        //条件构造器
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        TODO=实现条件获取动态

        return CommonResult.success(dynamics)
    }

    fun newDynamic(uid: Long, date: LocalDateTime, content: String, replyId: Long?): CommonResult<Dynamic> {
        private var newDynamic: Dynamic = new Dynamic()
        //是否回复
        if(replyId != null) {
            newDynamic.setReplyId(replyId)
        }else {
            newDynamic.setReplyId(Long.valueOf(-1))
        }
        newDynamic.setUid(uid).setDate(date).setContent(content)
        private val result: int = dynamicMapper.insert(newDynamic)
        if(result == 1) {
            return CommonResult.success(newDynamic)
        }else if(result < 1) {
            //插入失败
            logger.error("newDynamic: uid {}, date {}, {} 插入失败", uid, date, MESSAGE_DATABASE_ERROR)
            return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }else {
            //意外情况
            logger.error("newDynamic: uid {}, date {}, {} 插入行数大于1", did, date, MESSAGE_DATABASE_ERROR)
            return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }
    }

    fun deleteDynamic(did: Long): CommonResult<Dynamic> {
        private var dDynamic: Dynamic = dynamicMapper.selectById(did)
        if(dDynamic != null) {
            private val result: int = dynamicMapper.deleteById(did)
            if(result == 1) {
                return CommonResult.success(dDynamic)
            }else if(result < 1) {
                logger.info("deleteDynamic: did {}, {} 删除失败", did, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }else {
                //意外情况
                logger.error("deleteDynamic: did {}, {} 修改行数大于1", did, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }else {
            //无此动态
            logger.info("deleteDynamic: did {}, {}", did, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
            }
        }
    }
}