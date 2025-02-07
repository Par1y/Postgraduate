package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.Dynamic
import java.time.LocalDateTime

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
interface DynamicService {

    fun getDynamic(did: Long): CommonResult<Dynamic>

    fun getDynamicBy(uid: Long?, beginDate: LocalDateTime?, endDate: LocalDateTime?, replyId: Long?): CommonResult<List<Dynamic>>

    fun newDynamic(uid: Long, date: LocalDateTime, content: String, replyId: Long?): CommonResult<Dynamic>

    fun deleteDynamic(did: Long): CommonResult<Dynamic>
}