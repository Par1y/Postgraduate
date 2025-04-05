package cn.sicnu.postgraduate.springsecurity.filter

import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.jwt.JWTException
import cn.hutool.jwt.JWTUtil
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.exception.CustomException
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

@Component
class JwtAuthenticationTokenFilter(
    private val cacheManager: CacheManager
) : OncePerRequestFilter(), EnvironmentAware {
    private val logger = LoggerFactory.getLogger("JwtAuthFilter")
    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    private lateinit var environment: Environment

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    /**
     * JWT过滤器 * 每个请求只执行一次
     * 获取并解析JWT token
     * Redis缓存获取用户信息
     * 构造loginUser,封装进入authToken,存入SecurityContextHolder
     * */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // 1. 获取并验证Token
            val authHeader = request.getHeader(AUTH_HEADER) ?: run {
                filterChain.doFilter(request, response)
                return
            }

            if (!authHeader.startsWith(BEARER_PREFIX)) {
                throw JWTException("Authorization头必须以Bearer开头")
            }

            val token = authHeader.substring(BEARER_PREFIX.length).trim()

            // 2. 验证签名和配置
            val jwtKey = environment.getProperty("security.jwtKey") ?: run {
                logger.error("JWT密钥未配置")
                throw JWTException("服务器配置错误")
            }

            if (!JWTUtil.verify(token, jwtKey.toByteArray())) {
                throw JWTException("非法JWT签名")
            }

            // 3. 解析Token
            val jwt = JWTUtil.parseToken(token).apply {
                // 4. 提取并验证uid
                val uidStr = getPayload("uid")?.toString() ?: throw JWTException("缺少uid字段")
                val userId = uidStr.toLongOrNull() ?: throw JWTException("uid格式错误")

                // 5. 验证过期时间
                val expireTime = (getPayload("expire_time") as? Number)?.toLong()
                    ?: throw JWTException("缺少expire_time字段")

                if (LocalDateTime.now().isAfter(LocalDateTimeUtil.ofUTC(expireTime))) {
                    cacheManager.getCache("user")?.evict(userId.toString())
                    throw JWTException("JWT已过期")
                }

                // 6. 从缓存获取用户
                val user = cacheManager.getCache("user")?.get(userId.toString(), User::class.java)
                    ?: throw CustomException(404, "用户信息不存在")


            // 7. 设置安全上下文
            val loginUser = LoginUser(user)
            val authentication = UsernamePasswordAuthenticationToken(
                loginUser,
                loginUser.password,
                loginUser.authorities
            )
            SecurityContextHolder.getContext().authentication = authentication
}
            // 8. 继续过滤器链
            filterChain.doFilter(request, response)

        } catch (e: JWTException) {
            logger.error("JWT验证失败: ${e.message}")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败: ${e.message}")
        } catch (e: CustomException) {
            logger.error("业务异常: ${e.message}")
            response.sendError((e.getCode()?: -1), e.message)
        } catch (e: Exception) {
            logger.error("处理JWT时发生意外错误", e)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "处理JWT时发生错误")
        }
    }
}