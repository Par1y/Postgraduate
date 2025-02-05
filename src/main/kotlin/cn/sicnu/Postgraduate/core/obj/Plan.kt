package cn.sicnu.Postgraduate.core.obj

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/*
    Plan
 */
@Data
@TableName("`plan`")
public data class User {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private var pid: Long? = null

    @TableField
    private var uid: Long? = null

    @TableField(fill = FieldFill.INSERT)
    private date: LocalDateTime? = null

    @TableField
    private var content: String? = null


    //getter & setter
    public fun getPid(): Long? {
        return this.pid
    }

    public fun getUid(): Long? {
        return this.uid
    }
    
    public fun getDate(): Date? {
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