package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.User

interface UserService {

    fun getUser(uid: Long): User

    fun login(uid: Long, password: String): User

    fun newUser(username: String, password: String): User

    fun alterUser(uid: Long, username: String?, password: String?): User

    fun deleteUser(uid: Long): User
}
