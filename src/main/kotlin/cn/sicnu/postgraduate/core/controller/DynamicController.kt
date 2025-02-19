package cn.sicnu.postgraduate.core.controller

import cn.sicnu.postgraduate.core.entity.CommonResult
import java.time.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.DynamicService
import cn.sicnu.postgraduate.core.entity.Dynamic
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

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
    @Tag(name = "动态接口", description = "")
    class DynamicController(private val dynamicService: DynamicService) {
        companion object {
            //日志模块
            private val logger: Logger = LoggerFactory.getLogger(DynamicController::class.java)
        }

        @GetMapping("/{did}")
        @Operation(summary="查询计划", description = "路径传入did")
        fun getPlan(@PathVariable("did") did: Long): CommonResult<Dynamic> {
            return CommonResult.success(dynamicService.getDynamic(did))
        }

        @GetMapping("/")
        @Operation(summary="批量查询计划", description = "")
        fun getDynamicBy(
            @RequestParam("uid") uid: Long?,
            @RequestParam("beginDate") beginDate: Long?,
            @RequestParam("endDate") endDate: Long?,
            @RequestParam("replyId") replyId: Long?
            ): CommonResult<List<Dynamic>> {
            return CommonResult.success(dynamicService.getDynamicBy(uid, beginDate, endDate, replyId))
        }

        @PostMapping("/")
        @Operation(summary="新建计划", description = "")
        fun newDynamic(
            @RequestParam("uid") uid: Long,
            @RequestParam("date") date: Long,
            @RequestParam("content") content: String,
            @RequestParam("replyId") replyId: Long?
        ): CommonResult<Dynamic> {
            return CommonResult.success(dynamicService.newDynamic(uid, date, content, replyId))
        }

        @DeleteMapping("/{did}")
        @Operation(summary="删除计划", description = "路径传入did")
        fun deleteDynamic(@PathVariable("did") did: Long): CommonResult<Dynamic> {
            return CommonResult.success(dynamicService.deleteDynamic(did))
        }
    }
}