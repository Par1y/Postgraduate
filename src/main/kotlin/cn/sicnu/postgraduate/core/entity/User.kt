package cn.sicnu.postgraduate.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler
import java.io.Serializable

/*
    User，后端内部使用
 */
@TableName("users")
data class User(
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private var uid: Long? = null,

    @TableField
    private var username: String? = null,

    @TableField
    private var password: String? = null,

    @TableField(typeHandler = JacksonTypeHandler::class)
    private var roles: List<String>? = listOf("ROLE_USER")
): Serializable
{
    //getter & setter
    public fun getUid(): Long? {
        return this.uid
    }
    
    public fun getUsername(): String? {
        return this.username
    }

    public fun getPassword(): String? {
        return this.password
    }

    public fun getRoles(): List<String>? {
        return this.roles
    }

    public fun setUid(uid: Long): Unit {
        this.uid = uid
    }

    public fun setUsername(username: String): Unit {
        this.username = username
    }

    public fun setPassword(password: String): Unit {
        this.password = password
    }

    public fun setRoles(roles: List<String>): Unit {
        this.roles = roles
    }
}