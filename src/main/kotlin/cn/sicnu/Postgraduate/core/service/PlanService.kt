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
        private var plan: Plan = PlanMapper.selectById(pid)
        if(plan != null) {
            //返回
            return CommonResult.success(plan)
        }else{
            //无此计划
            logger.info("getPlan: pid {}, {}", pid, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MESSING)
        }
    }

    fun getPlanBy(uid: Long, beginDate: LocalDateTime?, endDate: LocalDateTime?): CommonResult<List<Plan>> {
        TODO=实现批量计划查找
    }

    fun newPlan(uid: Long, date: LocalDateTime, content: String): CommonResult<Plan> {
        private var newPlan: Plan = new Plan()
        newPlan.setUid(uid).setDate(date).setContent(content)
        private val result: int = PlanMapper.insert(newPlan)
        if(result == 1) {
            return CommonResult.success(newPlan)
        }else if(result < 1){
            //插入失败
            logger.error("newPlan: uid {}, date {}, {} 插入失败", uid, date, MESSAGE_DATABASE_ERROR)
            return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }else{
            //意外情况
            logger.error("newPlan: uid {}, date {}, {} 插入行数大于1", pid, date, MESSAGE_DATABASE_ERROR)
            return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }
    }

    fun alterPlan(pid: Long, date: LocalDateTime?, content: String?): CommonResult<Plan> {
        if(date == null && content == null) {
            logger.info("alterPlan: pid {}, {}", pid, MESSAGE_UNCHANGED)
            return CommonResult.error(CODE_UNCHANGED, MESSAGE_UNCHANGED)
        }
        private var updatePlan: Plan = PlanMapper.selectById(pid)
        if(updatePlan != null) {
            //比对修改内容
            if(date != null) updatePlan.setDate(date)
            if(content != null) updatePlan.setContent(content)
            //修改
            private val result = PlanMapper.updateById(updatePlan)
            if(result == 1) {
                return CommonResult.success(updatePlan)
            }else if(result < 1) {
                //修改失败
                logger.error("alterPlan: pid {}, {} 修改失败", pid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }else{
                //意外情况
                logger.error("alterPlan: pid {}, {} 修改行数大于1", pid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        }else{
            //无此计划
            logger.info("alterPlan: pid {}, {}", pid, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MESSING)
        }
    }

    fun deletePlan(pid: Long): CommonResult<Plan> {
        private var dPlan: Plan = PlanMapper.selectById(pid)
        if(dPlan != null) {
            private val result: int = PlanMapper.deleteById(pid)
            if(result == 1) {
                return CommonResult.success(dPlan)
            }else if(result < 1) {
                logger.info("deletePlan: pid {}, {} 删除失败", pid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }else{
                //意外情况
                logger.error("deletePlan: pid {}, {} 修改行数大于1", pid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }else{
            //无此计划
            logger.info("deletePlan: pid {}, {}", pid, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }
}