package cn.sicnu.postgraduate.springsecurity.service

import java.util.Objects
import cn.sicnu.postgraduate.core.mapper.UserMapper
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import cn.sicnu.postgraduate.core.entity.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class UserDetailsServiceImpl(private val userMapper: UserMapper): UserDetailsService {
    public fun loadUserByUid(uid: Long): UserDetails {
        val user: User? = userMapper.selectById(uid)
        if (Objects.isNull(user)) {
            throw UsernameNotFoundException("Uid not found")
        }
        TODO("查询对应权限信息")

        return LoginUser(user)
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        TODO("Not yet implemented")
    }
}