package cn.sicnu.postgraduate.core.controller

import cn.sicnu.postgraduate.core.entity.CommonResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.DynamicService
import cn.sicnu.postgraduate.core.entity.Dynamic
import cn.sicnu.postgraduate.core.entity.DynamicVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

/** /dynamic接口控制器
 * GET /{did} 查询单动态
 * getDynamic: DynamicVO
 *
 * GET / 按条件查询动态
 * getDynamicBy: List<DynamicVO>
 *
 *  POST / 新建动态
 *  newDynamic: DynamicVO
 *
 *  DELETE /{did} 删除动态
 *  deleteDynamic: DynamicVO
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
        @Operation(summary = "查询计划", description = "路径传入did")
        fun getDynamic(@PathVariable("did") did: String): CommonResult<DynamicVO> {
            return CommonResult.success(
                createDynamicVO(
                    dynamicService.getDynamic(did))
            )
        }

        @GetMapping("/")
        @Operation(summary = "批量查询计划", description = "")
        fun getDynamicBy(
            @RequestParam("uid") uid: String?,
            @RequestParam("beginDate") beginDate: String?,
            @RequestParam("endDate") endDate: String?,
            @RequestParam("replyId") replyId: String?
        ): CommonResult<List<DynamicVO>> {
            return CommonResult.success(
                createDynamicVOList(
                    dynamicService.getDynamicBy(uid, beginDate, endDate, replyId))
            )
        }

        @PostMapping("/")
        @Operation(summary = "新建计划", description = "")
        fun newDynamic(
            @RequestParam("uid") uid: String,
            @RequestParam("date") date: String,
            @RequestParam("content") content: String,
            @RequestParam("replyId") replyId: String?
        ): CommonResult<DynamicVO> {
            return CommonResult.success(
                createDynamicVO(
                    dynamicService.newDynamic(uid, date, content, replyId))
            )
        }

        @DeleteMapping("/{did}")
        @Operation(summary = "删除计划", description = "路径传入did")
        fun deleteDynamic(@PathVariable("did") did: String): CommonResult<DynamicVO> {
            return CommonResult.success(
                createDynamicVO(
                    dynamicService.deleteDynamic(did)
                )
            )
        }

        private fun createDynamicVO(dynamic: Dynamic): DynamicVO {
            return DynamicVO(
                did = dynamic.did.toString(),
                uid = dynamic.uid.toString(),
                date = dynamic.date,
                content = dynamic.content,
                replyId = dynamic.replyId.toString()
            )
        }

        private fun createDynamicVOList(dynamics: List<Dynamic>): List<DynamicVO> {
            return dynamics.map { createDynamicVO(it) }
        }
    }
}