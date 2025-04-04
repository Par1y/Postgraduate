package cn.sicnu.postgraduate.core.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.lang.Nullable
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
/**
 * 全局异常处理
 * @author Par1y
 */
@RestControllerAdvice
class GlobalExceptionHandler: ResponseEntityExceptionHandler() {

    /**
     * 捕获自定义异常
     */
    @ExceptionHandler(CustomException::class)
    fun customExceptionHandler(
        request: HttpServletRequest,
        e: Exception,
        response: HttpServletResponse
    ): ErrorResponseEntity {
        response.status = HttpStatus.BAD_REQUEST.value()
        val exception = e as CustomException
        return ErrorResponseEntity(exception.getCode(), exception.message)
    }

    /**
     * 捕获 RuntimeException 异常
     */
    @ExceptionHandler(RuntimeException::class)
    fun runtimeExceptionHandler(
        request: HttpServletRequest,
        e: Exception,
        response: HttpServletResponse
    ): ErrorResponseEntity {
        response.status = HttpStatus.BAD_REQUEST.value()
        val exception = e as RuntimeException
        return ErrorResponseEntity(400, exception.message)
    }

    /**
     * 通用的接口映射异常处理方法
     */
    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        @Nullable body: Any?,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return when (ex) {
            is MethodArgumentNotValidException -> {
                ResponseEntity(
                    ErrorResponseEntity(status.value(), ex.bindingResult.allErrors[0].defaultMessage),
                    status
                )
            }
            is MethodArgumentTypeMismatchException -> {
                logger.error("参数转换失败，方法：${ex.parameter.method?.name}，参数：${ex.name},信息：${ex.localizedMessage}")
                ResponseEntity(
                    ErrorResponseEntity(status.value(), "参数转换失败"),
                    status
                )
            }
            else -> {
                ResponseEntity(
                    ErrorResponseEntity(status.value(), "参数转换失败"),
                    status
                )
            }
        }
    }
}
