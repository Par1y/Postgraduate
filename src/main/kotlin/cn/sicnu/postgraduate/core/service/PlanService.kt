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
    fun getPlan(pid: String): Plan

    fun getPlanBy(uid: String, beginDate: String?, endDate: String?): List<Plan>

    fun newPlan(uid: String, date: String, content: String): Plan

    fun alterPlan(pid: String, date: String?, content: String?): Plan

    fun deletePlan(pid: String): Plan
}
