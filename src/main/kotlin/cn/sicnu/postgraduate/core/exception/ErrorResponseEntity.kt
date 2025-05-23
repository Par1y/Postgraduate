package cn.sicnu.postgraduate.core.exception

/**
 * 错误响应体
 * @author Par1y
 */
class ErrorResponseEntity(
    private var code: Int? = null,
    private var message: String? = null
) {
    public  fun getCode(): Int? {
        return code
    }

    public fun getMessage(): String? {
        return message
    }

    public fun setCode(code: Int): Unit {
        this.code = code
    }

    public  fun setMessage(message: String): Unit {
        this.message = message
    }
}