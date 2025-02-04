package cn.sicnu.Postgraduate.core.controller

import org.springframework.boot.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.Postgraduate.service.UserService

/*
    /user接口控制器

    GET /{uid} 查询用户
    getUser: commonResult<UserVO>
    
    POST / 用户登录
    login: commonResult<UserVO>

    POST /{uid} 用户修改
    alterUser: commonResult<UserVO>
 */
@RestController
@RequestMapping("/v1/user")
class UserController {
    companion object{
        //日志模块
        private final logger: Logger = LoggerFactory.getLogger(userController.class)
    }

    @Autowired
    private userService: UserService

    @GetMapping("/{uid}")
    fun getUser(@PathVariable uid: Long): commonResult<UserVO> {
        return userService.getUser(uid)
    }

    @PostMapping("/")
    fun login(@RequestParam("uid") uid: Long, @RequestParam("password") password: String): commonResult<UserVO> {
        return userService.verifyUser(uid, password)
    }

    @PostMapping("/{uid}")
    fun alterUser(@PathVariable uid: Long @RequestParam("username") username: String?, @RequestParam("password") password: String?): commonResult<UserVO> {
        return userService.alterUser(uid, username, password)
    }

    @DeleteMapping("/{uid}")
    fun deleteUser(@PathVariable uid: Long) {
        return userService.deleteUser(uid)
    }
}