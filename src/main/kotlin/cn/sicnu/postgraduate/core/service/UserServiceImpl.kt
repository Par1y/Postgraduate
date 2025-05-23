package cn.sicnu.postgraduate.core.service

import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import org.springframework.cache.annotation.*
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import cn.sicnu.postgraduate.core.mapper.UserMapper
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.exception.CustomException
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

/**
 * 用户服务实现
 */
@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val authenticationManager: AuthenticationManager,
    private val cacheManager: CacheManager,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
): UserService, EnvironmentAware {
    companion object {
        //日志模块
        private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

        //常量声明
        private const val CODE_SUCCESS: Int = 0
        private const val CODE_MISSING: Int = -1
        private const val MESSAGE_MISSING: String = "用户不存在"
        private const val CODE_UNCHANGED: Int = -3
        private const val MESSAGE_UNCHANGED: String = "未改动"
        private const val CODE_DATABASE_ERROR: Int = -4
        private const val MESSAGE_DATABASE_ERROR: String = "数据库错误"

        private const val CODE_PERMISSION_DENIED: Int = -2
        private const val MESSAGE_PERMISSION_DENIED: String = "无权限"
    }

    private lateinit var environment: Environment

    @Autowired
    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    @Cacheable(value = ["user"], key = "#uidStr")
    override fun getUser(uidStr: String): User {
        //参数转换
        val uid: Long = Convert.toLong(uidStr)

        val user: User? = userMapper.selectById(uid)
        return if (user != null) {
            user
        } else {
            logger.info("getUser: uid {}, {}", uid, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @Cacheable(value = ["user"], key = "#uidStr")
    override fun login(uidStr: String, password: String): User {
        //参数转换
        val uid: Long = Convert.toLong(uidStr)

        password.let { encrypt(password) }
        val authenticationToken: UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(uid, password)
        val authenticate: Authentication = authenticationManager.authenticate(authenticationToken)
        //认证不通过
        if(Objects.isNull(authenticate)) {
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
        //认证通过
        val principal: LoginUser = authenticate.principal as LoginUser
        val user: User = principal.getUser()!!
        return user
    }

    @CachePut(value = ["user"], key = "T(java.lang.String).valueOf(#result.uid)")
    override fun newUser(username: String, password: String): User {
        val defaultRoleStr = environment.getProperty("security.defaultRole")
        val defaultRole = defaultRoleStr?.split(",")
        val snowFlake:  Snowflake = IdUtil.getSnowflake()
        val uid: Long = snowFlake.nextId()
        val newUser: User = User().apply {
            setUid(uid)
            setUsername(username)
            setPassword(encrypt(password))
            defaultRole?.let { setRoles(it) }
        }
        val result: Int = userMapper.insert(newUser)
        when (result) {
            1 -> {
                return newUser
            }
            in Int.MIN_VALUE..0 -> {
                logger.error("newUser: username {}, {} 插入失败", username, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
            else -> {
                logger.error("newUser: username {}, {} 插入行数大于1", username, MESSAGE_DATABASE_ERROR)
                throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        }
    }

    @CachePut(value = ["user"], key = "#uidStr")
    override fun alterUser(uidStr: String, username: String?, password: String?): User {
        //参数转换
        val forwardUid: Long = Convert.toLong(uidStr)

        //取得用户
        val user: User = getUser()
        val uid: Long? = user.getUid()

        //鉴权
        if(uid != forwardUid) {
            logger.error("alterUser: forwardUid {}, realUid: {}, {}", forwardUid, uid, MESSAGE_PERMISSION_DENIED)
            throw CustomException(CODE_PERMISSION_DENIED, "我去黑客来袭")
        }

        if (username == null && password == null) {
            logger.info("alterUser: uid {}, {}", uid, MESSAGE_UNCHANGED)
            throw CustomException(CODE_UNCHANGED, MESSAGE_UNCHANGED)
        }
        var updateUser: User? = userMapper.selectById(uid)
        if (updateUser != null) {
            username?.let {
                updateUser.setUsername(it)
                it
            }
            password?.let {
                updateUser.setPassword(encrypt(it))
                it
            }
            val result: Int = userMapper.updateById(updateUser)
            when (result) {
                1 -> {
                    return updateUser
                }
                in Int.MIN_VALUE..0 -> {
                    logger.error("alterUser: uid {}, {} 修改失败", uid, MESSAGE_DATABASE_ERROR)
                    throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
                else -> {
                    logger.error("alterUser: uid {}, {} 修改行数大于1", uid, MESSAGE_DATABASE_ERROR)
                    throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
            }
        } else {
            logger.info("alterUser: uid {}, {}", uid, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

//    @CacheEvict(value = ["user"], key = "T(java.lang.String).valueOf(#result.uid)")
    override fun deleteUser(): User {
        //获取用户信息
        val user = getUser()
        val uid: Long? = user.getUid()
        val result = userMapper.deleteById(uid)

        when (result) {
            1 -> {
                //清除缓存
                cacheManager.getCache("user")?.evict(uid.toString())
                return user
            }
                in Int.MIN_VALUE..0 -> {
                    logger.error("deleteUser: uid {}, {} 删除失败", uid, MESSAGE_DATABASE_ERROR)
                    throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
                else -> {
                    logger.error("deleteUser: uid {}, {} 删除行数大于1", uid, MESSAGE_DATABASE_ERROR)
                    throw CustomException(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
            }
    }

    @CacheEvict(value=["user"], key = "T(java.lang.String).valueOf(#result.uid)")
    override fun logout(): User {
        //获取用户信息
        val user = getUser()

        // 注解删除Redis缓存
        return user
    }

    private fun encrypt(password: String): String {
        return bCryptPasswordEncoder.encode(password)
    }

    private fun getUser(): User {
        // 获取用户信息
        val auth = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        val loginUser = auth.principal as LoginUser
        val user = loginUser.getUser() ?: throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        return user
    }
}
