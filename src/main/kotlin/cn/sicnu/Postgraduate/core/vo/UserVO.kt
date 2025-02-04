package cn.sicnu.Postgraduate.core.vo

/*
    UserVO，前端返回用
 */
public data class UserVO {
    private var uid: Long? = null
    private var username: String? = null

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