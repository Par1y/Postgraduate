package cn.sicnu.postgraduate.core.controller

import java.time.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.PlanService
import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.Plan

/*
    /plan接口控制器

    GET /{pid} 查询单计划
    getPlan: commonResult<Plan>
    
    GET / 按条件查询计划
    getPlanBy: commonResult<List<Plan>>

    POST / 新建计划
    newPlan: commonResult<Plan>

    POST /{pid} 计划修改
    alterPlan: commonResult<Plan>
 */
@RestController
@RequestMapping("/v1/plan")
class PlanController(private val planService: PlanService) {
    companion object{
        //日志模块
        private val logger: Logger = LoggerFactory.getLogger(PlanController::class.java)
    }

    @GetMapping("/{pid}")
    fun getPlan(@PathVariable("pid") pid: Long): CommonResult<Plan> {
        return planService.getPlan(pid)
    }

    @GetMapping("/")
    fun getPlanBy(
        @RequestParam uid: Long,
        @RequestParam beginDate: LocalDateTime?,
        @RequestParam endDate: LocalDateTime?
        ): CommonResult<List<Plan>> {
        return planService.getPlanBy(uid, beginDate, endDate)
    }

    @PostMapping("/")
    fun newPlan(
        @RequestParam uid: Long,
        @RequestParam date: LocalDateTime,
        @RequestParam content: String
        ): CommonResult<Plan> {
        return planService.newPlan(uid, date, content)
    }

    @PostMapping("/{pid}")
    fun alterPlan(@PathVariable("pid") pid: Long,
    @RequestParam date: LocalDateTime?,
    @RequestParam content: String?
    ): CommonResult<Plan> {
        return planService.alterPlan(pid, date, content)
    }

    @DeleteMapping("/{pid}")
    fun deletePlan(@PathVariable("pid") pid: Long): CommonResult<Plan> {
        return planService.deletePlan(pid)
    }
}