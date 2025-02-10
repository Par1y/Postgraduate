package cn.sicnu.postgraduate.springsecurity.entity

import cn.sicnu.postgraduate.core.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

public data class  LoginUser(
    private var user: User? = null
): UserDetails {
    public fun loginUser(user: User?): Unit {
        this.user = user
    }

    // 返回用户权限
    override fun getAuthorities(): MutableList<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        user?.getRoles()?.forEach { role ->
            // 将每个角色转换为 SimpleGrantedAuthority
            authorities.add(SimpleGrantedAuthority(role))
        }
        return authorities
    }

    public fun getUser(): User? {
        return user
    }

    public override fun getUsername(): String? {
        return user?.getUsername()
    }

    public override fun getPassword(): String? {
        return user?.getPassword()
    }

    public fun getUid(): Long? {
        return user?.getUid()
    }

    public fun setUsername(username: String): Unit {
        user?.setUsername(username)
    }

    public fun setPassword(password: String): Unit {
        user?.setPassword(password)
    }

    public fun setUid(uid: Long): Unit {
        user?.setUid(uid)
    }
}