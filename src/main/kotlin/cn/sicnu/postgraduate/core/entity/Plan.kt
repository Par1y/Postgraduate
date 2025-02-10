package cn.sicnu.postgraduate.core.entity

import java.time.LocalDateTime
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/*
    Plan
 */
@TableName("plan")
data class Plan (
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private var pid: Long? = null,

    @TableField
    private var uid: Long? = null,

    // @TableField(fill = FieldFill.INSERT)
    private var date: LocalDateTime? = null,

    @TableField
    private var content: String? = null
) {
    //getter & setter
    public fun getPid(): Long? {
        return this.pid
    }

    public fun getUid(): Long? {
        return this.uid
    }
    
    public fun getDate(): LocalDateTime? {
        return this.date
    }

    public fun getContent(): String? {
        return this.content
    }

    public fun setPid(pid: Long): Unit {
        this.pid = pid
    }

    public fun setUid(uid: Long): Unit {
        this.uid = uid
    }

    public fun setDate(date: LocalDateTime): Unit {
        this.date = date
    }

    public fun setContent(content: String): Unit {
        this.content = content
    }
}