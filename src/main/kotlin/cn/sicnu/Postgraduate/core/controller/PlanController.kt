package cn.sicnu.Postgraduate.core.controller

import org.springframework.boot.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.Postgraduate.service.PlanService

/*
    /plan接口控制器

    GET /{pid} 按pid查询计划
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
class PlanController {
    companion object{
        //日志模块
        private final logger: Logger = LoggerFactory.getLogger(planController.class)
    }

    @Autowired
    private planService: PlanService

    @GetMapping("/{pid}")
    fun getPlan(@PathVariable pid: Long): commonResult<PlanVO> {
        return planService.getPlan(pid)
    }

    @GetMapping("/")
    fun getPlanBy() {
        TODO=uid或时间段等查找
    }

    @PostMapping("/")
    fun newPlan() {
        TODO=新建计划
    }

    @PostMapping("/{pid}")
    fun alterPlan() {
        TODO=修改计划
    }

    @DeleteMapping("/{pid}")
    fun deletePlan(@PathVariable pid: Long) {
        return planService.deletePlan(pid)
    }
}