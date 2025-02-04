package cn.sicnu.Postgraduate.core.vo

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("`user`")
/*
    User，后端内部使用
 */
public data class User {
    @TableId(value = "id", type = IdType.INPUT)
    private var uid: Long? = null

    @TableField
    private var username: String? = null

    @TableField
    private var password: String? = null

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

    public fun setUid(uid: Long): Unit {
        this.uid = uid
    }

    public fun setUsername(username: String): Unit {
        this.username = username
    }

    public fun setPassword(password: String): Unit {
        this.password = password
    }
}