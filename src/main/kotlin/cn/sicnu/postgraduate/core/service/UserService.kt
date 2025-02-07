package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.CommonResult
import cn.sicnu.postgraduate.core.entity.UserVO

interface UserService {

    fun getUser(uid: Long): CommonResult<UserVO>

    fun login(uid: Long, password: String): CommonResult<Any>

    fun newUser(username: String, password: String): CommonResult<Any>

    fun alterUser(uid: Long, username: String?, password: String?): CommonResult<UserVO>

    fun deleteUser(uid: Long): CommonResult<UserVO>
}
