package cn.sicnu.postgraduate.core.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.swagger.v3.oas.annotations.media.Schema

/*
    UserVO，前端返回用
 */
@Schema(description = "用户展示实体类", name = "UserVO")
data class UserVO(
    @JsonSerialize(using = ToStringSerializer::class)
    @Schema(description = "用户id", name = "uid", maxLength = 64, minLength = 64)
    private var uid: Long? = null,
    @Schema(description = "用户名", name = "username", maxLength = 64)
    private var username: String? = null
) {
    //getter & setter
    public fun getUid(): Long? {
        return uid
    }
    
    public fun getUsername(): String? {
        return username
    }

    public fun setUid(uid: Long): Unit {
        this.uid = uid
    }

    public fun setUsername(username: String): Unit {
        this.username = username
    }
}