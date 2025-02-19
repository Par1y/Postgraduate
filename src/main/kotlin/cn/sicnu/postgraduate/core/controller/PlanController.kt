package cn.sicnu.postgraduate.core.controller

import cn.sicnu.postgraduate.core.entity.CommonResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.PlanService
import cn.sicnu.postgraduate.core.entity.Plan
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

/**
    plan接口控制器

    GET /{pid} 查询单计划
    getPlan: Plan
    
    GET / 按条件查询计划
    getPlanBy: commonResult<List<Plan>>

    POST / 新建计划
    newPlan: Plan

    POST /{pid} 计划修改
    alterPlan: Plan

    DELETE /{pid} 删除计划
    deletePlan: Plan
 */
@RestController
@RequestMapping("/v1/plan")
@Tag(name = "计划接口", description = "")
class PlanController(private val planService: PlanService) {
    companion object{
        //日志模块
        private val logger: Logger = LoggerFactory.getLogger(PlanController::class.java)
    }

    @GetMapping("/{pid}")
    @Operation(summary="查询计划", description = "路径传入pid")
    fun getPlan(@PathVariable("pid") pid: Long): CommonResult<Plan> {
        return CommonResult.success(planService.getPlan(pid))
    }

    @GetMapping("/")
    @Operation(summary="批量查询计划", description = "")
    fun getPlanBy(
        @RequestParam("uid") uid: Long,
        @RequestParam("beginDate") beginDate: Long?,
        @RequestParam("endDate") endDate: Long?
        ): CommonResult<List<Plan>> {
        return CommonResult.success(planService.getPlanBy(uid, beginDate, endDate))
    }

    @PostMapping("/")
    @Operation(summary="新建计划", description = "")
    fun newPlan(
        @RequestParam("uid") uid: Long,
        @RequestParam("date") date: Long,
        @RequestParam("content") content: String
        ): CommonResult<Plan> {
        return CommonResult.success(planService.newPlan(uid, date, content))
    }

    @PostMapping("/{pid}")
    @Operation(summary="修改计划", description = "路径传入pid")
    fun alterPlan(@PathVariable("pid") pid: Long,
    @RequestParam("date") date: Long?,
    @RequestParam("content") content: String?
    ): CommonResult<Plan> {
        return CommonResult.success(planService.alterPlan(pid, date, content))
    }

    @DeleteMapping("/{pid}")
    @Operation(summary="删除计划", description = "路径传入pid")
    fun deletePlan(@PathVariable("pid") pid: Long): CommonResult<Plan> {
        return CommonResult.success(planService.deletePlan(pid))
    }
}