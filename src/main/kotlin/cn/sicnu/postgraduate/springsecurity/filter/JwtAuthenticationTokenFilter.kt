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
import org.springframework.security.core.GrantedAuthority
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
        val jwtKey = environment.getProperty("security.jwtKey") ?: run {
            logger.error("JWT密钥未配置")
            throw JWTException("服务器配置错误")
        }

        val token = request.getHeader("token")?.takeIf { it.isNotBlank() } ?: run {
            filterChain.doFilter(request, response)
            return
        }

        // 1. 验证签名
        if (!JWTUtil.verify(token, jwtKey.toByteArray())) {
            throw JWTException("非法JWT签名")
        }

        try {
            // 2. 解析Token
            val jwt = JWTUtil.parseToken(token)

            // 3. 提取uid (作为字符串处理)
            val uidStr = jwt.getPayload("uid") as? String ?: throw JWTException("缺少uid字段")
            val userId = uidStr.toLongOrNull() ?: throw JWTException("uid格式错误")

            // 4. 提取并转换expire_time
            val expireTimeObj = jwt.getPayload("expire_time") as? Number ?: throw JWTException("缺少expire_time字段")
            val expiryDate = LocalDateTimeUtil.ofUTC(expireTimeObj.toLong())

            logger.debug("解析JWT成功: uid=$userId, 过期时间=$expiryDate")

            // 5. 验证过期时间
            if (LocalDateTime.now().isAfter(expiryDate)) {
                cacheManager.getCache("user")?.evictIfPresent(userId)
                throw JWTException("JWT已过期")
            }

            // 6. 从缓存获取用户
            val user = cacheManager.getCache("user")?.get(userId)?.get() as? User
                ?: throw CustomException(404, "用户信息不存在")

            // 7. 设置安全上下文
            val loginUser = LoginUser(user)
            val authentication = UsernamePasswordAuthenticationToken(
                loginUser,
                loginUser.password,
                loginUser.authorities
            )
            SecurityContextHolder.getContext().authentication = authentication

            // 8. 继续过滤器链
            filterChain.doFilter(request, response)

        } catch (e: ClassCastException) {
            logger.error("JWT声明类型错误", e)
            throw JWTException("JWT声明类型不匹配")
        } catch (e: NumberFormatException) {
            logger.error("数字格式错误", e)
            throw JWTException("JWT声明数字格式错误")
        } catch (e: JWTException) {
            throw e // 重新抛出已知的JWT异常
        } catch (e: Exception) {
            logger.error("处理JWT时发生意外错误", e)
            throw JWTException("处理JWT时发生错误: ${e.message}")
        }
    }
}