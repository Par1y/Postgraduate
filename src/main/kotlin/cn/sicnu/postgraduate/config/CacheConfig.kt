import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.boot.ApplicationRunner
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        // 创建 ObjectMapper 并配置 Kotlin 支持
        val objectMapper = ObjectMapper().apply {
            registerModule(kotlinModule()) // 使用 kotlinModule() 替代直接构造 KotlinModule
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().build(),
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )
        }

        // 配置 Redis 缓存
        val config = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper)))
            .disableCachingNullValues()
            .entryTtl(Duration.ofHours(24))

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build()
    }

    // 启动时自动清除缓存
    @Bean
    fun clearCacheOnStartup(redisConnectionFactory: RedisConnectionFactory): ApplicationRunner {
        return ApplicationRunner { _ ->
            val redisTemplate = RedisTemplate<String, Any>().apply {
                connectionFactory = redisConnectionFactory
                keySerializer = StringRedisSerializer()
                afterPropertiesSet() // 初始化
            }

            listOf("user", "plan", "dynamic").forEach { cacheName ->
                redisTemplate.keys("$cacheName::*")?.let { keys ->
                    if (keys.isNotEmpty()) {
                        redisTemplate.delete(keys)
                        println("Cleared ${keys.size} entries for cache: $cacheName")
                    }
                }
            }
        }
    }
}