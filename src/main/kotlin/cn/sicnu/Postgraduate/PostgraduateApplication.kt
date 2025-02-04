package cn.sicnu.Postgraduate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("cn.sicnu.Postgraduate.core.mapper")
class PostgraduateApplication

fun main(args: Array<String>) {
	runApplication<PostgraduateApplication>(*args)
}
