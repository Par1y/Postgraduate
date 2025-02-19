package cn.sicnu.postgraduate.core.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

/*
    统一结果返回类
    **工具类**
 */
@Schema(description = "通用结果返回类", hidden = true)
class CommonResult<T> : Serializable {
    companion object {
        private val CODE_SUCCESS: Int = 0
        private val CODE_UNKNOWN_ERR: Int = -255
        private val MESSAGE_UNKNOWN_ERR: String = "未知错误"

        // 规范返回对象
        fun <T> error(result: CommonResult<*>): CommonResult<T> {
            val code: Int = result.code ?: CODE_UNKNOWN_ERR
            val message: String = result.message ?: MESSAGE_UNKNOWN_ERR
            return error(code, message)
        }

        fun <T> error(code: Int, message: String): CommonResult<T> {
            require(!CODE_SUCCESS.equals(code)) { "code不为错误码" }
            val result = CommonResult<T>()
            result.code = code
            result.message = message
            return result
        }

        fun <T> success(data: T): CommonResult<T> {
            val result = CommonResult<T>()
            result.code = CODE_SUCCESS
            result.data = data
            result.message = "成功"
            return result
        }
    }

    private var code: Int? = null
    private var message: String? = null
    private var data: T? = null

    @JsonIgnore
    private fun isSuccess(): Boolean {
        return CODE_SUCCESS.equals(code)
    }

    @JsonIgnore
    private fun isError(): Boolean {
        return !isSuccess()
    }

    fun getCode(): Int? {
        return this.code
    }

    fun getMessage(): String? {
        return this.message
    }

    fun getData(): T? {
        return this.data
    }

    fun setCode(code: Int) {
        this.code = code
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setData(data: T) {
        this.data = data
    }
}
