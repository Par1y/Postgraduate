package cn.sicnu.postgraduate.core.entity

import java.time.LocalDateTime
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema

/*
    Dynamic
 */
@Schema(description = "动态实体类", name = "Dynamic")
@TableName("dynamic")
data class DynamicVO(
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "动态id", name = "did")
    var did: String? = null,

    @TableField
    @Schema(description = "创建动态的用户id", name = "uid")
    var uid: String? = null,

    @TableField
    @Schema(description = "动态创建时间", name = "date")
    var date: LocalDateTime? = null,

    @TableField
    @Schema(description = "动态内容", name = "content")
    var content: String? = null,

    @TableField
    @Schema(description = "回复的源动态id，可缺省", name = "replyId")
    var replyId: String? = null
)