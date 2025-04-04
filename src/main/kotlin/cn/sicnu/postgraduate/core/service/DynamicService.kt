package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.Dynamic

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
interface DynamicService {

    fun getDynamic(did: String): Dynamic

    fun getDynamicBy(uid: String?, beginDate: String?, endDate: String?, replyId: String?): List<Dynamic>

    fun newDynamic(uid: String, date: String, content: String, replyId: String?): Dynamic

    fun deleteDynamic(did: String): Dynamic
}