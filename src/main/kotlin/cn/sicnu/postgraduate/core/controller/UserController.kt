package cn.sicnu.postgraduate.core.controllPlaner

import cn.hutool.jwt.JWTUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import cn.sicnu.postgraduate.core.service.UserServiceImpl
import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.entity.UserVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
    /user接口控制器

    GET /{uid} 查询用户
    getUser: CommonResult<UserVO>
    
    POST / 用户登录\注册\登出
    login: CommonResult<UserVO>
    register: CommonResult<UserVO>
    logout: CommonResult<UserVO>

    POST /{uid} 用户修改
    alterUser: CommonResult<UserVO>

    DELETE / 用户删除
    deleteUser: CommonResult<UserVO>
 */
@RestController
@RequestMapping("/v1/user")
@Tag(name = "用户接口", description = "")
class UserController(private val userService: UserServiceImpl): EnvironmentAware {

    companion object {
        // 日志模块
        private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

        private const val CODE_WRONG_PARAM: Int = -21
        private const val MESSAGE_WRONG_PARAM: String = "请求参数错误"
    }

    private lateinit var environment: Environment

    @Autowired
    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    @GetMapping("/{uid}")
    @Operation(summary="查询用户", description = "路径传入uid")
    fun getUser(@PathVariable("uid") uid: Long): CommonResult<UserVO> {
        return CommonResult.success(
            createUserVO(
                userService.getUser(uid)
            )
        )
    }

    @PostMapping("/")
    @Operation(summary="复合，注册/登录/登出用户", description = "uid+password登录  \n" +
            "username+password注册  \n" +
            "仅uid登出")
    fun loginNRegisterNlogout(
        @RequestParam("uid") uid: Long?,
        @RequestParam("username") username: String?,
        @RequestParam("password") password: String?
    ): CommonResult<Any> {
        return if (uid != null && username == null && password != null) {
            /* 登录 */
            val user = userService.login(uid, password)
            val vo: UserVO = createUserVO(user)

            /* 构建JWT */
            val jwtKey = environment.getProperty("security.jwtKey")
            val jwtMap: Map<String, Any> = HashMap<String, Any>().apply {
                put("uid", vo.getUid()!!)
                val expireTime = LocalDateTime.now().plusWeeks(1)    //过期时间一周
                val milliseconds = expireTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                put("expire_time", milliseconds)
            }
            val jwt: String = JWTUtil.createToken(jwtMap, jwtKey?.toByteArray())

            /* 存入redis
            * 已注解实现 */

            /* 构建响应体payload */
            val map: Map<String, String> = HashMap<String, String>().apply {
                put("token",jwt)
                put("UserVO", vo.toString())
            }
            CommonResult.success(map)
        } else if (uid == null && username != null && password != null) {
            /* 注册 */
            CommonResult.success(
                createUserVO(
                    userService.newUser(username, password)
                )
            )
        }else if(uid != null && username == null && password == null) {
            /* 登出 */
            val user: User = userService.logout()
            CommonResult.success(user)
        } else {
            logger.info("loginNRegisterNlogout: uid {}, username {}, {}", uid, username, MESSAGE_WRONG_PARAM)
            CommonResult.error(CODE_WRONG_PARAM, MESSAGE_WRONG_PARAM)
        }
    }

    @PostMapping("/{uid}")
    @Operation(summary="修改用户", description = "路径传入uid，参数传入欲修改username或password")
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

    @DeleteMapping("/")
    @Operation(summary="删除用户", description = "无需传参，根据登录状态解析用户")
    fun deleteUser(): CommonResult<UserVO> {
        return CommonResult.success(
            createUserVO(
                userService.deleteUser()
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
