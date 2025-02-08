package cn.sicnu.postgraduate.springsecurity.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import cn.sicnu.postgraduate.springsecurity.filter.JwtAuthenticationTokenFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val userDetailsService: UserDetailsService // 注入 UserDetailsService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher("/api/v1/**")
            csrf(disable)
            authorizeRequests {
                authorize(anyRequest, authenticated)
            }
            httpBasic {}
            addFilterBefore(JwtAuthenticationTokenFilter::class.java, UsernamePasswordAuthenticationFilter::class.java)
            formLogin {
//                loginPage = "/login"
            }
        }
        return http.build()
    }
}
