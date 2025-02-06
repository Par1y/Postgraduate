package cn.sicnu.Postgraduate.core.controller

import org.springframework.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.Postgraduate.core.service.UserService
import cn.sicnu.Postgraduate.core.obj.CommonResult
import cn.sicnu.Postgraduate.core.obj.UserVO

/*
    /user接口控制器

    GET /{uid} 查询用户
    getUser: CommonResult<UserVO>
    
    POST / 用户登录\注册
    login: CommonResult<UserVO>
    register: CommonResult<UserVO>

    POST /{uid} 用户修改
    alterUser: CommonResult<UserVO>

    DELETE /{uid} 用户删除
    deleteUser: CommonResult<UserVO>
 */
@RestController
@RequestMapping("/v1/user")
class UserController(private val userService: UserService) {

    companion object {
        // 日志模块
        private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)
        private const val CODE_WRONG_PARAM: Int = -21
        private const val MESSAGE_WRONG_PARAM: String = "请求参数错误"
    }

    @GetMapping("/{uid}")
    fun getUser(@PathVariable("uid") uid: Long): CommonResult<UserVO> {
        return userService.getUser(uid)
    }

    @PostMapping("/")
    fun loginNRegister(
        @RequestParam("uid") uid: Long?,
        @RequestParam("username") username: String?,
        @RequestParam("password") password: String
    ): CommonResult<UserVO> {
        return if (uid != null && username == null) {
            userService.verifyUser(uid, password)
        } else if (uid == null && username != null) {
            userService.newUser(username, password)
        } else {
            logger.info("loginNRegister: uid {}, username {}, {}", uid, username, MESSAGE_WRONG_PARAM)
            CommonResult.error(CODE_WRONG_PARAM, MESSAGE_WRONG_PARAM)
        }
    }

    @PostMapping("/{uid}")
    fun alterUser(
        @PathVariable("uid") uid: Long,
        @RequestParam("username") username: String?,
        @RequestParam("password") password: String?
    ): CommonResult<UserVO> {
        return userService.alterUser(uid, username, password)
    }

    @DeleteMapping("/{uid}")
    fun deleteUser(@PathVariable("uid") uid: Long): CommonResult<UserVO> {
        return userService.deleteUser(uid)
    }
}
