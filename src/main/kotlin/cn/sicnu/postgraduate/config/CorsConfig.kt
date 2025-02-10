package cn.sicnu.postgraduate.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/** 跨域配置
 */

@Configuration
class CorsConfig: WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry): Unit {
        //允许跨域路径
        registry.addMapping("/**")
            //允许跨域请求的域名
            .allowedOriginPatterns("*")
            //允许cookie
            .allowCredentials(true)
            //允许的请求方式
            .allowedMethods("GET", "POST", "DELETE", "PUT")
            //允许的Header属性
            .allowedHeaders("*")
            //允许时间
            .maxAge(3600)
    }
}