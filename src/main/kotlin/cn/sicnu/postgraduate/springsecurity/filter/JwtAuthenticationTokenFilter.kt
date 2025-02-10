package cn.sicnu.postgraduate.springsecurity.filter

import cn.hutool.core.convert.NumberWithFormat
import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.jwt.JWT
import cn.hutool.jwt.JWTException
import cn.hutool.jwt.JWTUtil
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.exception.CustomException
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime
import java.util.*

/** JWT过滤器
 * 每个请求只执行一次
 * 获取并解析JWT token
 * Redis缓存获取用户信息
 * 构造loginUser,封装进入authToken,存入SecurityContextHolder
 */

@Component
class JwtAuthenticationTokenFilter(
    private val cacheManager: CacheManager
): OncePerRequestFilter(), EnvironmentAware {
    companion object {
        //logger
        private val logger: Logger = LoggerFactory.getLogger("JwtAuthentication::class.java")
    }

    private lateinit var environment: Environment

    @Autowired
    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        /* token获取解析 */
        val jwtKey = environment.getProperty("security.jwtKey")
        var uid: Long = 0L
        var expireTime: LocalDateTime = LocalDateTime.now().plusDays(1)
        val token = request.getHeader("token")
        if(!StringUtils.hasText(token)) {
            //没有token
            filterChain.doFilter(request, response)
            return
        }
        if(!JWTUtil.verify(token, jwtKey?.toByteArray())) throw JWTException("非法JWT")
        try {
            val tokenPayload: JWT = JWTUtil.parseToken(token)
            uid = tokenPayload.let{
                val tmp: NumberWithFormat = it.getPayload("uid") as NumberWithFormat
                tmp.toLong()
            }
            expireTime = tokenPayload.let {
                val tmp: NumberWithFormat = it.getPayload("expire_time") as NumberWithFormat
                val result = LocalDateTimeUtil.ofUTC(tmp.toLong())
                result
            }
        }catch(e: Exception) {
            logger.error(e)
            throw JWTException("JWT内容错误")
        }
        if(LocalDateTime.now().isAfter(expireTime)) {
            val cache: Cache? = cacheManager.getCache("user")
            cache?.let { it.evictIfPresent(uid) }
            throw JWTException("过期的JWT")
        }
        /*redis获取用户信息*/
        val user: User? = cacheManager.getCache("user")?.get(uid)?.get() as? User
        if(Objects.isNull(user)) throw CustomException(404, "用户未登录")

        /* 加入SecurityContextHolder */
        //构造loginUser
        val loginUser: LoginUser = LoginUser(user)
        //权限处理等
        var credentials: Any? = null
        var authToken: UsernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(loginUser, credentials, null)
        SecurityContextHolder.getContext().setAuthentication(authToken)

        filterChain.doFilter(request, response)
    }
}