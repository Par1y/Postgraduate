package cn.sicnu.postgraduate.core.entity

import java.time.LocalDateTime
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/*
    Dynamic
 */
@TableName("dynamic")
data class Dynamic(
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    var did: Long? = null,

    @TableField
    var uid: Long? = null,

    @TableField
    var date: LocalDateTime? = null,

    @TableField
    var content: String? = null,

    @TableField
    var replyId: Long? = null
)
//{
//    /* getter&setter */
//    public fun getDid(): Long? {
//        return did
//    }
//
//    public fun getUid(): Long? {
//        return uid
//    }
//
//    public fun getDate(): LocalDateTime? {
//        return date
//    }
//
//    public fun getContent(): String? {
//        return content
//    }
//
//    public fun getReplyId(): Long? {
//        return replyId
//    }
//
//    public fun setDid(did: Long) {
//        this.did  = did
//    }
//
//    public fun setUid(uid: Long) {
//        this.uid = uid
//    }
//
//    public fun setDate(date: LocalDateTime) {
//        this.date = date
//    }
//
//    public fun setContent(content: String) {
//        this.content = content
//    }
//
//    public fun setReplyId(replyId: Long) {
//        this.replyId = replyId
//    }
//}