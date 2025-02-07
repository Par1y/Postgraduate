package cn.sicnu.Postgraduate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("cn.sicnu.Postgraduate.core.mapper")
@EnableCaching
class PostgraduateApplication

fun main(args: Array<String>) {
	runApplication<PostgraduateApplication>(*args)
}
