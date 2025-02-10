package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.Plan

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
interface PlanService{
    fun getPlan(pid: Long): Plan

    fun getPlanBy(uid: Long, beginDate: Long?, endDate: Long?): List<Plan>

    fun newPlan(uid: Long, date: Long, content: String): Plan

    fun alterPlan(pid: Long, date: Long?, content: String?): Plan

    fun deletePlan(pid: Long): Plan
}
