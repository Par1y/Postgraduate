package cn.sicnu.postgraduate.core.exception

/** 自定义异常类
 * @author Par1y
 */
class CustomException(
    private var code: Int? = null,
    message: String? = null
) : RuntimeException(message) { // 直接在主构造函数中调用父类构造函数

    companion object {
        private const val serialVersionUID: Long = 4564124491192825748L
    }

    public fun getCode(): Int? {
        return code
    }

    public fun setCode(code: Int) {
        this.code = code
    }
}
