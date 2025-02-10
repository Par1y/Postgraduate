package cn.sicnu.postgraduate.core.controller

import java.time.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.DynamicService
import cn.sicnu.postgraduate.core.entity.Dynamic

/** /dynamic接口控制器
 * GET /{did} 查询单动态
 * getDynamic: Dynamic
 *
 * GET / 按条件查询动态
 * getDynamicBy: List<Dynamic>
 *
 *  POST / 新建动态
 *  newDynamic: Dynamic
 *
 *  DELETE /{did} 删除动态
 *  deleteDynamic: Dynamic
 */
class DynamicController {
    @RestController
    @RequestMapping("/v1/dynamic")
    class DynamicController(private val dynamicService: DynamicService) {
        companion object {
            //日志模块
            private val logger: Logger = LoggerFactory.getLogger(DynamicController::class.java)
        }

        @GetMapping("/{did}")
        fun getPlan(@PathVariable("did") did: Long): Dynamic {
            return dynamicService.getDynamic(did)
        }

        @GetMapping("/")
        fun getDynamicBy(
            @RequestParam("uid") uid: Long?,
            @RequestParam("beginDate") beginDate: LocalDateTime?,
            @RequestParam("endDate") endDate: LocalDateTime?,
            @RequestParam("replyId") replyId: Long?
            ): List<Dynamic> {
            return dynamicService.getDynamicBy(uid, beginDate, endDate, replyId)
        }

        @PostMapping("/")
        fun newDynamic(
            @RequestParam("uid") uid: Long,
            @RequestParam("date") date: LocalDateTime,
            @RequestParam("content") content: String,
            @RequestParam("replyId") replyId: Long?
        ): Dynamic {
            return dynamicService.newDynamic(uid, date, content, replyId)
        }

        @DeleteMapping("/{did}")
        fun deleteDynamic(@PathVariable("did") did: Long): Dynamic {
            return dynamicService.deleteDynamic(did)
        }
    }
}