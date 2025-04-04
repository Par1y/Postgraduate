package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.User

/**
 *  用户服务
 *
 *   查询用户
 *   getUser: User，成功包装对象，失败空对象&错误信息
 *   getUserBy: List<User>，成功包装对象，失败空对象&错误信息
 *
 *   用户登入
 *   login: User
 *
 *  新建用户
 *  newUser: User，成功包装对象，失败空对象&错误信息
 *
 *   修改用户
 *  alterUser: User，成功包装对象，失败空对象&错误信息

 *   删除用户
 *  deleteUser: User，成功包装对象，失败空对象&错误信息
 *
 *   用户登出
 *  logout: User
 */
interface UserService {

    /**
     * 查询用户
     * @param   uidStr      用户id
     */
    fun getUser(uidStr: String): User

    /**
     * 登录
     * @param   uidStr      用户id
     * @param  password        密码
     */
    fun login(uidStr: String, password: String): User

    /**
     * 注册
     * @param  username        用户名
     * @param  password        密码
     */
    fun newUser(username: String, password: String): User

    /**
     * 修改用户
     * @param  uidStr      用户id
     * @param  username        用户名
     * @param  password        密码
     */
    fun alterUser(uidStr: String, username: String?, password: String?): User

    /**
     * 删除用户
     * 无参数
     */
    fun deleteUser(): User


    /**
     * 登出
     * 无参数
     */
    fun logout(): User
}
