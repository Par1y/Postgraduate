package cn.sicnu.postgraduate.core.controller

import cn.sicnu.postgraduate.core.entity.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.PlanService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

/**
    plan接口控制器

    GET /{pid} 查询单计划
    getPlan: PlanVO
    
    GET / 按条件查询计划
    getPlanBy: commonResult<List<PlanVO>>

    POST / 新建计划
    newPlan: PlanVO

    POST /{pid} 计划修改
    alterPlan: PlanVO

    DELETE /{pid} 删除计划
    deletePlan: PlanVO
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
    fun getPlan(@PathVariable("pid") pid: String): CommonResult<PlanVO> {
        return CommonResult.success(
            createPlanVO(
                planService.getPlan(pid))
        )
    }

    @GetMapping("/")
    @Operation(summary="批量查询计划", description = "")
    fun getPlanBy(
        @RequestParam("uid") uid: String,
        @RequestParam("beginDate") beginDate: String?,
        @RequestParam("endDate") endDate: String?
        ): CommonResult<List<PlanVO>> {
        return CommonResult.success(
            createPlanVOList(
                planService.getPlanBy(uid, beginDate, endDate)
            )
        )
    }

    @PostMapping("/")
    @Operation(summary="新建计划", description = "")
    fun newPlan(
        @RequestParam("uid") uid: String,
        @RequestParam("date") date: String,
        @RequestParam("content") content: String
        ): CommonResult<PlanVO> {
        return CommonResult.success(
            createPlanVO(
                planService.newPlan(uid, date, content)
            )
        )
    }

    @PostMapping("/{pid}")
    @Operation(summary="修改计划", description = "路径传入pid")
    fun alterPlan(@PathVariable("pid") pid: String,
    @RequestParam("date") date: String?,
    @RequestParam("content") content: String?
    ): CommonResult<PlanVO> {
        return CommonResult.success(
            createPlanVO(
                planService.alterPlan(pid, date, content))
        )
    }

    @DeleteMapping("/{pid}")
    @Operation(summary="删除计划", description = "路径传入pid")
    fun deletePlan(@PathVariable("pid") pid: String): CommonResult<PlanVO> {
        return CommonResult.success(
            createPlanVO(
                planService.deletePlan(pid)
            )
        )
    }

    private fun createPlanVO(plan: Plan): PlanVO {
        return PlanVO().apply {
            setPid(plan.getPid().toString()!!)
            setUid(plan.getUid().toString()!!)
            setDate(plan.getDate()!!)
            setContent(plan.getContent()!!)
        }
    }

    private fun createPlanVOList(plans: List<Plan>): List<PlanVO> {
        return plans.map { createPlanVO(it) }
    }
}