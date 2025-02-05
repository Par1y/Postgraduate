package cn.sicnu.Postgraduate.core.controller

import org.springframework.boot.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.Postgraduate.service.UserService

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
class UserController {
    companion object{
        //日志模块
        private final logger: Logger = LoggerFactory.getLogger(userController.class)
        private final CODE_WRONG_PARAM: Integer = Integer.valueOf(-21)
        private final val MESSAGE_WRONG_PARAM: String = "请求参数错误"
    }

    @Autowired
    private userService: UserService

    @GetMapping("/{uid}")
    fun getUser(@PathVariable uid: Long): commonResult<UserVO> {
        return userService.getUser(uid)
    }

    @PostMapping("/")
    fun loginNRegister(@RequestParam("uid") uid: Long?, @RequestParam username: String?, @RequestParam("password") password: String): commonResult<UserVO> {
        if(uid != null && username == null) {
            return userService.verifyUser(uid, password)
        }else if(uid == null && username != null) {
            return userService.newUser(username, password)
        }else{
            logger.info("loginNRegister: uid {}, username {}, {}", uid, username, MESSAGE_WRONG_PARAM)
            return CommonResult.error(CODE_WRONG_PARAM, MESSAGE_WRONG_PARAM)
        }
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