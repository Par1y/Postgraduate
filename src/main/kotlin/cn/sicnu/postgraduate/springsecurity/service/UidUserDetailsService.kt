package cn.sicnu.postgraduate.springsecurity.service

import cn.sicnu.postgraduate.core.mapper.UserMapper
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UidUserDetailsService(
    private val userMapper: UserMapper
): UserDetailsService {
    override fun loadUserByUsername(uidString: String?): UserDetails {
        val uid = uidString?.toLongOrNull() ?: throw UsernameNotFoundException("UID is not a valid number")
        val user = userMapper.selectById(uid)
        return LoginUser(user)
    }

    fun loadUserByUid(uid: Long): UserDetails {
        val user = userMapper.selectById(uid)
        return LoginUser(user)
    }
}