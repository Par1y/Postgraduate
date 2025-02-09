package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.User

/*
    用户服务

    查询用户
    getUser: User，成功包装对象，失败空对象&错误信息
    getUserBy: List<User>，成功包装对象，失败空对象&错误信息

    用户登入
    login: User

    新建用户
    newUser: User，成功包装对象，失败空对象&错误信息

    修改用户
    alterUser: User，成功包装对象，失败空对象&错误信息

    删除用户
    deleteUser: User，成功包装对象，失败空对象&错误信息

    用户登出
    logout: User
 */
interface UserService {

    fun getUser(uid: Long): User

    fun login(uid: Long, password: String): User

    fun newUser(username: String, password: String): User

    fun alterUser(uid: Long, username: String?, password: String?): User

    fun deleteUser(uid: Long): User

    fun logout(): User
}
