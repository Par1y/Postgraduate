package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.Plan
import java.time.LocalDate
import java.time.LocalDateTime

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
interface PlanService{
    fun getPlan(pid: Long): CommonResult<Plan>

    fun getPlanBy(uid: Long, beginDate: LocalDateTime?, endDate: LocalDateTime?): CommonResult<List<Plan>>

    fun newPlan(uid: Long, date: LocalDateTime, content: String): CommonResult<Plan>

    fun alterPlan(pid: Long, date: LocalDateTime?, content: String?): CommonResult<Plan>

    fun deletePlan(pid: Long): CommonResult<Plan>
}
