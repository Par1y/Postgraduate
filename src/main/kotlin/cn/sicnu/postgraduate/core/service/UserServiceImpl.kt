package cn.sicnu.postgraduate.core.service

import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import cn.sicnu.postgraduate.core.mapper.UserMapper
import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.entity.UserVO
import cn.sicnu.postgraduate.core.entity.CommonResult

@Service
class UserServiceImpl(private val userMapper: UserMapper): UserService {
    companion object {
        //日志模块
        private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

        //常量声明
        private const val CODE_SUCCESS: Int = 0
        private const val CODE_MISSING: Int = -1
        private const val MESSAGE_MISSING: String = "用户不存在"
        private const val CODE_WRONG_PASSWORD: Int = -2
        private const val MESSAGE_WRONG_PASSWORD: String = "密码错误"
        private const val CODE_UNCHANGED: Int = -3
        private const val MESSAGE_UNCHANGED: String = "未改动"
        private const val CODE_DATABASE_ERROR: Int = -4
        private const val MESSAGE_DATABASE_ERROR: String = "数据库错误"
    }

    @Cacheable(value = ["user"], key = "#uid")
    override fun getUser(uid: Long): CommonResult<UserVO> {
        val user: User? = userMapper.selectById(uid)
        return if (user != null) {
            CommonResult.success(createUserVO(user))
        } else {
            logger.info("getUser: uid {}, {}", uid, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @Cacheable(value = ["user"], key = "#uid")
    override fun verifyUser(uid: Long, password: String): CommonResult<UserVO> {
        val user: User? = userMapper.selectById(uid)
        return if (user != null) {
            if (user.getPassword() == password) {
                CommonResult.success(createUserVO(user))
            } else {
                logger.info("verifyUser: uid {}, {}", uid, MESSAGE_WRONG_PASSWORD)
                CommonResult.error(CODE_WRONG_PASSWORD, MESSAGE_WRONG_PASSWORD)
            }
        } else {
            logger.info("verifyUser: uid {}, {}", uid, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @CachePut(value = ["user"], key = "#username")
    override fun newUser(username: String, password: String): CommonResult<UserVO> {
        val newUser: User = User().apply {
            setUsername(username)
            setPassword(password)
        }
        val result: Int = userMapper.insert(newUser)
        return when (result) {
            1 -> {
                val vo: UserVO = createUserVO(newUser)
                CommonResult.success(vo)
            }
            in Int.MIN_VALUE..0 -> {
                logger.error("newUser: username {}, {} 插入失败", username, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
            else -> {
                logger.error("newUser: username {}, {} 插入行数大于1", username, MESSAGE_DATABASE_ERROR)
                CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        }
    }

    @CachePut(value = ["user"], key = "#uid")
    override fun alterUser(uid: Long, username: String?, password: String?): CommonResult<UserVO> {
        if (username == null && password == null) {
            logger.info("alterUser: uid {}, {}", uid, MESSAGE_UNCHANGED)
            return CommonResult.error(CODE_UNCHANGED, MESSAGE_UNCHANGED)
        }
        var updateUser: User? = userMapper.selectById(uid)
        return if (updateUser != null) {
            username?.let { updateUser.setUsername(it) }
            password?.let { updateUser.setPassword(it) }
            val result: Int = userMapper.updateById(updateUser)
            when (result) {
                1 -> {
                    val vo: UserVO = createUserVO(updateUser)
                    CommonResult.success(vo)
                }
                in Int.MIN_VALUE..0 -> {
                    logger.error("alterUser: uid {}, {} 修改失败", uid, MESSAGE_DATABASE_ERROR)
                    CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
                else -> {
                    logger.error("alterUser: uid {}, {} 修改行数大于1", uid, MESSAGE_DATABASE_ERROR)
                    CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
            }
        } else {
            logger.info("alterUser: uid {}, {}", uid, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    @CacheEvict(value = ["user"], key = "#uid")
    override fun deleteUser(uid: Long): CommonResult<UserVO> {
        val user: User? = userMapper.selectById(uid)
        return if (user != null) {
            val result: Int = userMapper.deleteById(uid)
            when (result) {
                1 -> {
                    val vo: UserVO = createUserVO(user)
                    CommonResult.success(vo)
                }
                in Int.MIN_VALUE..0 -> {
                    logger.error("deleteUser: uid {}, {} 删除失败", uid, MESSAGE_DATABASE_ERROR)
                    CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
                else -> {
                    logger.error("deleteUser: uid {}, {} 删除行数大于1", uid, MESSAGE_DATABASE_ERROR)
                    CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
                }
            }
        } else {
            logger.info("deleteUser: uid {}, {}", uid, MESSAGE_MISSING)
            CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    private fun createUserVO(user: User): UserVO {
        return UserVO().apply {
            setUid(user.getUid()!!)
            setUsername(user.getUsername()!!)
        }
    }
}
