package cn.sicnu.Postgraduate.core.service

import org.springframework.boot.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/*
    计划服务

    查询计划
    getPlan: CommResult<Plan>，成功包装对象，失败空对象&错误信息
    getPlanBy: CommonResult<List<Plan>>，成功包装对象，失败空对象&错误信息

    新建计划
    newPlan: CommonResult<Plan>，成功包装对象，失败空对象&错误信息

    修改计划
    alterPlan: CommonResult<Plan>，成功包装对象，失败空对象&错误信息

    删除计划
    deletePlan: CommonResult<Plan>，成功包装对象，失败空对象&错误信息
 */
@Service
class UserService {
    companion object{
        //日志模块
        private final logger: Logger = LoggerFactory.getLogger(userService.class)

        //常量声明
        private final CODE_SUCCESS: Integer = Integer.valueOf(0)
        private final CODE_MISSING: Integer = Integer.valueOf(-1)
        private final val MESSAGE_MISSING: String = "计划不存在"

        private final CODE_UNCHANGED: Integer = Integer.valueOf(-3)
        private final val MESSAGE_UNCHANGED: String = "未改动"
        private final CODE_DATABASE_ERROR: Integer = Integer.valueOf(-4)
        private final val MESSAGE_DATABASE_ERROR: String = "数据库错误"
    }

    fun getPlan(pid: Long): commonResult<Plan> {
        TODO=实现单计划查找
    }

    fun getPlanBy(uid: Long, beginDate: LocalDateTime?, endDate: LocalDateTime?): CommonResult<List<Plan>> {
        TODO=实现批量计划查找
    }

    fun newPlan(uid: Long, date: LocalDateTime, content: String): CommonResult<Plan> {
        TODO=实现新建计划
    }

    fun alterPlan(pid: Long, date: LocalDateTime?, content: String?): CommonResult<Plan> {
        TODO=实现修改计划
    }

    fun deletePlan(pid: Long): CommonResult<Plan> {
        TODO=实现删除计划
    }
}