package cn.sicnu.postgraduate.core.entity

import java.time.LocalDateTime
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/*
    Dynamic
 */
@TableName("`dynamic`")
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