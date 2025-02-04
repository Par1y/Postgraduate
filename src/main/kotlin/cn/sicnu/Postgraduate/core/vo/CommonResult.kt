package cn.sicnu.Postgraduate.core.vo

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.Assert;
import java.io.Serializable;

/*
    统一结果返回类
    **工具类**
 */
public class CommonResult<T> implements Serializable {
    companion object {
        private final val Integer CODE_SUCCESS = Integer.valueOf(0)

        //规范返回对象
        public fun <T> error(result: CommonResult<*>): CommonResult<T> {
            return error(result.code, result.message)
        }

        public fun <T> error(code: Integer, message: String) {
            Assert.isTrue(!CODE_SUCCESS.equals(code), "code不为错误码")
            result: CommonResult<T> = new CommonResult<>()
            result.code = code
            result.message = message
            return result
        }

        public fun <T> success(T data): CommonResult<T> {
            result: CommonResult<T> = new CommonResult<>()
            result.code = CODE_SUCCESS;
            result.data = data
            result.message = "成功"
            return result
        }
    }

    private var code: Integer? = null
    private var message: String? = null
    private var data: T? = null

    @JsonIgnore
    private isSuccess(): Boolean {
        return CODE_SUCCESS.equals(code)
    }

    @JsonIgnore
    private isError(): Boolean {
        return !isSuccess()
    }

    public getCode():Integer? {
        return this.code
    }

    public getMessage(): String? {
        return this.message
    }

    public getData(): T? {
        return this.data
    }

    public setCode(code: Integer): Unit {
        this.code = code
    }

    public setMessage(message: String): Unit {
        this.message = message
    }

    public setData(data: T): Unit {
        this.data = data
    }
}