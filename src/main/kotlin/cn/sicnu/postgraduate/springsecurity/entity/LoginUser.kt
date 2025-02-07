package cn.sicnu.postgraduate.springsecurity.entity

import cn.sicnu.postgraduate.core.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

public class  LoginUser(user: User?) : UserDetails {

    private var user: User = User()

    public override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO()
    }

    public override fun getUsername(): String? {
        return user.getUsername()
    }

    public override fun getPassword(): String? {
        return user.getPassword()
    }

    public fun getUid(): Long? {
        return user.getUid()
    }

    public fun setUsername(username: String): Unit {
        user.setUsername(username)
    }

    public fun setPassword(password: String): Unit {
        user.setPassword(password)
    }

    public fun setUid(uid: Long): Unit {
        user.setUid(uid)
    }
}