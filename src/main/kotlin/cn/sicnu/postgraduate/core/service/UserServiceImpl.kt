package cn.sicnu.postgraduate.core.service

import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import cn.hutool.jwt.JWTUtil
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import cn.sicnu.postgraduate.core.mapper.UserMapper
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.entity.UserVO
import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.exception.CustomException
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import kotlin.collections.HashMap

@Service
class UserServiceImpl(private val userMapper: UserMapper,
    private val authenticationManager: AuthenticationManager
): UserService {
    companion object {
        //日志模块
        private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

        //常量声明
        @Value("\${security.jwtKey}")
        private lateinit  var jwtKey: String
        private const val CODE_SUCCESS: Int = 0
        private const val CODE_MISSING: Int = -1
        private const val MESSAGE_MISSING: String = "用户不存在"
        private const val CODE_WRONG_PASSWORD: Int = -2
        private const val MESSAGE_WRONG_PASSWORD: String = "密码错误"
        private const val CODE_UNCHANGED: Int = -3
        private const val MESSAGE_UNCHANGED: String = "未改动"
        private const val CODE_DATABASE_ERROR: Int = -4
        private const val MESSAGE_DATABASE_ERROR: String = "数据库错误"
    }

    @Cacheable(value = ["user"], key = "#uid")
    override fun getUser(uid: Long): User {
        val user: User? = userMapper.selectById(uid)
        return if (user != null) {
            user
        } else {
            logger.info("getUser: uid {}, {}", uid, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @Cacheable(value = ["user"], key = "#uid")
    override fun login(uid: Long, password: String): User {
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

    @CachePut(value = ["user"], key = "#uid")
    override fun newUser(username: String, password: String): User {
        val snowFlake:  Snowflake = IdUtil.getSnowflake()
        val uid: Long = snowFlake.nextId()
        val newUser: User = User().apply {
            setUid(uid)
            setUsername(username)
            setPassword(password)
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

    @CachePut(value = ["user"], key = "#uid")
    override fun alterUser(uid: Long, username: String?, password: String?): User {
        if (username == null && password == null) {
            logger.info("alterUser: uid {}, {}", uid, MESSAGE_UNCHANGED)
            throw CustomException(CODE_UNCHANGED, MESSAGE_UNCHANGED)
        }
        var updateUser: User? = userMapper.selectById(uid)
        if (updateUser != null) {
            username?.let { updateUser.setUsername(it) }
            password?.let { updateUser.setPassword(it) }
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

    @CacheEvict(value = ["user"], key = "#uid")
    override fun deleteUser(): User {
        //获取用户信息
        val auth: UsernamePasswordAuthenticationToken = SecurityContextHolder.getContext().getAuthentication() as UsernamePasswordAuthenticationToken
        val loginUser: LoginUser = auth.getPrincipal() as LoginUser
        val user: User? = loginUser.getUser()
        val uid: Long = user?.getUid() ?: 0
        if (user != null) {
            val result: Int = userMapper.deleteById(uid)
            when (result) {
                1 -> {
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
        } else {
            logger.info("deleteUser: uid {}, {}", uid, MESSAGE_MISSING)
            throw CustomException(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @CacheEvict(value=["user"], key="#uid")
    override fun logout(): User {
        //获取用户信息
        val auth: UsernamePasswordAuthenticationToken = SecurityContextHolder.getContext().getAuthentication() as UsernamePasswordAuthenticationToken
        val loginUser: LoginUser = auth.getPrincipal() as LoginUser
        val user: User = loginUser.getUser() ?: User()
        // 注解删除Redis缓存
        return user
    }
}
