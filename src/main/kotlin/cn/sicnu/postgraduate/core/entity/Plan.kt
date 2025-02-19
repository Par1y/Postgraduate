package cn.sicnu.postgraduate.core.entity

import java.time.LocalDateTime
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema

/*
    Plan
 */
@TableName("plan")
@Schema(description = "计划实体类", name = "Plan")
data class Plan (
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "计划id", name = "pid")
    private var pid: Long? = null,

    @TableField
    @Schema(description = "创建计划的用户id", name = "uid")
    private var uid: Long? = null,

    // @TableField(fill = FieldFill.INSERT)
    @Schema(description = "计划创建日期", name = "date")
    private var date: LocalDateTime? = null,

    @TableField
    @Schema(description = "计划内容", name = "content")
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