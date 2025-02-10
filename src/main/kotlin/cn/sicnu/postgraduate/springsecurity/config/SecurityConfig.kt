package cn.sicnu.postgraduate.springsecurity.config

import cn.sicnu.postgraduate.springsecurity.filter.JwtAuthenticationTokenFilter
import cn.sicnu.postgraduate.springsecurity.service.UidUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val userDetailsService: UidUserDetailsService, // 注入 UserDetailsService
    private val jwtAuthenticationTokenFilter: JwtAuthenticationTokenFilter // 注入自定义 JWT 过滤器
) {

    // 配置 BCrypt 密码加密器
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // 配置 AuthenticationManager
    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        builder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
        return builder.build()
    }

    // 配置 SecurityFilterChain
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // 禁用 CSRF
            .csrf { it.disable() }
            // 设置会话管理为无状态（STATELESS）
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // 配置 API 的授权规则
            .authorizeHttpRequests { auth ->
                // API端口要求认证
                auth.requestMatchers("/api/v1/**").authenticated()
                // 其他请求放行
                auth.anyRequest().permitAll()
            }
            // 启用 HTTP 基本认证
            .httpBasic { }
            // 添加自定义 JWT 认证过滤器，并设置顺序
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            // 启用跨域配置
            .cors { }

        return http.build()
    }
}

