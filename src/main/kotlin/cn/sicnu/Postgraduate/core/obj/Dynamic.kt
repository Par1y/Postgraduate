package cn.sicnu.Postgraduate.core.obj

import java.time.LocalDateTime
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/*
    Dynamic
 */
@Data
@TableName("`dynamic`")
public data class Dynamic {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private var did: Long? = null

    @TableField
    private var uid: Long? = null

    // @TableField(fill = FieldFill.INSERT)
    private date: LocalDateTime? = null

    @TableField
    private var content: String? = null

    @TableField
    private var replyId: Long? = null

    //getter & setter
    public fun getDid(): Long? {
        return this.did
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

    public fun getReplyDid(): Long? {
        return this.replyId
    }

    public fun setDid(did: Long): Unit {
        this.did = did
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

    public fun setReplyId(replyId: Long): Unit {
        this.replyId = replyId
    }
}