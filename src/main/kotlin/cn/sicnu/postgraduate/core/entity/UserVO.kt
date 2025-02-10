package cn.sicnu.postgraduate.core.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer

/*
    UserVO，前端返回用
 */
data class UserVO(
    @JsonSerialize(using = ToStringSerializer::class)
    private var uid: Long? = null,
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