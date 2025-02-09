package cn.sicnu.postgraduate.core.controller

import cn.hutool.jwt.JWTUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.UserServiceImpl
import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.entity.UserVO
import org.springframework.beans.factory.annotation.Value
import java.time.LocalDateTime

/*
    /user接口控制器

    GET /{uid} 查询用户
    getUser: CommonResult<UserVO>
    
    POST / 用户登录\注册\登出
    login: CommonResult<UserVO>
    register: CommonResult<UserVO>
    logout: CommonResult<UserVO>

    POST /{uid} 用户修改
    alterUser: CommonResult<UserVO>

    DELETE /{uid} 用户删除
    deleteUser: CommonResult<UserVO>
 */
@RestController
@RequestMapping("/v1/user")
class UserController(private val userService: UserServiceImpl) {

    companion object {
        // 日志模块
        private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

        @Value("\${security.jwtKey}")
        private lateinit var jwtKey: String
        private const val CODE_WRONG_PARAM: Int = -21
        private const val MESSAGE_WRONG_PARAM: String = "请求参数错误"
    }

    @GetMapping("/{uid}")
    fun getUser(@PathVariable("uid") uid: Long): CommonResult<UserVO> {
        return CommonResult.success(
            createUserVO(
                userService.getUser(uid)
            )
        )
    }

    @PostMapping("/")
    fun loginNRegisterNlogout(
        @RequestParam("uid") uid: Long?,
        @RequestParam("username") username: String?,
        @RequestParam("password") password: String
    ): CommonResult<Any> {
        return if (uid != null && username == null && password != null) {
            /* 登录 */
            val user = userService.login(uid, password)
            val vo: UserVO = createUserVO(user)

            /* 构建JWT */
            val jwtMap: Map<String, Any> = HashMap<String, Any>().apply {
                put("uid", vo.getUid()!!)
                put("expire_time", LocalDateTime.now().plusWeeks(1))    //过期时间一周
            }
            val jwt: String = JWTUtil.createToken(jwtMap, jwtKey.toByteArray())

            /* 存入redis
            * 已注解实现 */

            /* 构建响应体payload */
            val map: Map<String, String> = HashMap<String, String>().apply {
                put("token",jwt)
                put("UserVO", vo.toString())
            }
            return CommonResult.success(map)

        } else if (uid == null && username != null && password != null) {
            /* 注册 */
            CommonResult.success(
                createUserVO(
                    userService.newUser(username, password)
                )
            )
        }else if(uid != null && username == null && password == null) {
            /* 登出 */
            userService.logout(uid)
            CommonResult.success()
        } else {
            logger.info("loginNRegisterNlogout: uid {}, username {}, {}", uid, username, MESSAGE_WRONG_PARAM)
            CommonResult.error(CODE_WRONG_PARAM, MESSAGE_WRONG_PARAM)
        }
    }

    @PostMapping("/{uid}")
    fun alterUser(
        @PathVariable("uid") uid: Long,
        @RequestParam("username") username: String?,
        @RequestParam("password") password: String?
    ): CommonResult<UserVO> {
        return CommonResult.success(
            createUserVO(
                userService.alterUser(uid, username, password)
            )
        )
    }

    @DeleteMapping("/{uid}")
    fun deleteUser(@PathVariable("uid") uid: Long): CommonResult<UserVO> {
        return CommonResult.success(
            createUserVO(
                userService.deleteUser(uid)
            )
        )
    }

    private fun createUserVO(user: User): UserVO {
        return UserVO().apply {
            setUid(user.getUid()!!)
            setUsername(user.getUsername()!!)
        }
    }
}
