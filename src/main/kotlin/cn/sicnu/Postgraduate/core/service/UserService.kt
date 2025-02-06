package cn.sicnu.Postgraduate.core.service

import org.springframework.boot.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/*
    用户服务

    查询得到用户
    getUser: CommResult<UserVO>，成功包装对象，失败空对象&错误信息
    
    验证用户名密码
    verifyUser: CommonResult<UserVO>，成功包装对象，失败空对象&错误信息

    添加用户
    newUser: CommonResult<UserVO>，成功包装对象，失败空对象&错误信息

    修改用户
    alterUser: CommonResult<UserVO>，成功包装对象，失败空对象&错误信息

    删除用户
    deleteUser: CommonResult<UserVO>，成功包装对象，失败空对象&错误信息
 */
@Service
class UserService {
    companion object{
        //日志模块
        private final logger: Logger = LoggerFactory.getLogger(userService.class)

        //常量声明
        private final CODE_SUCCESS: Integer = Integer.valueOf(0)
        private final CODE_MISSING: Integer = Integer.valueOf(-1)
        private final val MESSAGE_MISSING: String = "用户不存在"
        private final CODE_WRONG_PASSWORD: Integer = Integer.valueOf(-2)
        private final val MESSAGE_WRONG_PASSWORD: String = "密码错误"
        private final CODE_UNCHANGED: Integer = Integer.valueOf(-3)
        private final val MESSAGE_UNCHANGED: String = "未改动"
        private final CODE_DATABASE_ERROR: Integer = Integer.valueOf(-4)
        private final val MESSAGE_DATABASE_ERROR: String = "数据库错误"
    }

    @Autowired
    private userMapper: UserMapper

    fun getUser(uid: Long): CommonResult<UserVO> {
        private var user: User = userMapper.selectById(uid)
        if(user != null) {
            //返回
            private var vo: UserVO = new UserVO()
            vo.setUid(user.getUid()).setUsername(user.getUsername())
            return CommonResult.success(vo)
        }else{
            //查无此人
            logger.info("getUser: uid {}, {}", uid, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }
    
    fun verifyUser(uid: Long, password: String): CommonResult<UserVO> {
        private var user: User = userMapper.selectById(uid)
        if(user != null) {
            //验证密码一致
            if(user.password.equals(password)) {
                private var vo: UserVO = new UserVO()
                vo.setUid(user.getUid()).setUsername(user.getUsername())
                return CommonResult.success(vo)
            }else{
                //密码不一致
                logger.info("verifyUser: uid {}, {}", uid, MESSAGE_WRONG_PASSWORD)
                return CommonResult.error(CODE_WRONG_PASSWORD, MESSAGE_WRONG_PASSWORD)
            }
        }else{
            //查无此人
            logger.info("verifyUser: uid {}, {}", uid, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    fun newUser(username: String, password: String) {
        private var newUser: User = new User()
        newUser.setUsername(username).setPassword(password)
        private val result: int = userMapper.insert(newUser)
        if(result == 1) {
            return CommonResult.success(newPlan)
        }else if(result < 1){
            //插入失败
            logger.error("newUser: username {}, {} 插入失败", username, MESSAGE_DATABASE_ERROR)
            return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }else{
            //意外情况
            logger.error("newUser: username {}, {} 插入行数大于1", username, MESSAGE_DATABASE_ERROR)
            return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
        }
    }

    fun alterUser(uid: Long, username: String?, password: String?): CommonResult<UserVO> {
        //未改动
        if(username == null && password == null) {
            logger.info("alterUser: uid {}, {}", uid, MESSAGE_UNCHANGED)
            return CommonResult.error(CODE_UNCHANGED, MESSAGE_UNCHANGED)
        }
        private var updateUser: User = userMapper.selectById(uid)
        if(updateUser != null) {
            //比对修改内容
            if(username != null) updateUser.setUsername(username)
            if(password != null) updateUser.setPassword(password)
            //修改
            private val result: int = userMapper.updateById(updateUser)
            if(result == 1) {
                private var vo: UserVO = new UserVO()
                vo.setUid(updateUser.getUid()).setUsername(updateUser.getUsername())
                return CommonResult.success(vo)
            }else if(result < 1) {
                //修改失败
                logger.error("alterUser: uid {}, {} 修改失败", uid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }else {
                //意外情况
                logger.error("alterUser: uid {}, {} 修改行数大于1", uid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        }else{
            //查无此人
            logger.info("alterUser: uid {}, {}", uid, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MISSING)
        }
    }

    fun deleteUser(uid: Long): CommonResult<UserVO> {
        private var user: User = userMapper.selectById(uid)
        if(user != null) {
            private val result: int = userMapper.deleteById(uid)
            if(result == 1) {
                private var vo: UserVO = new UserVO()
                vo.setUid(user.getUid()).setUsername(user.getUsername())
                return CommonResult.success(vo)
            }else if(result < 1){
                //删除失败
                logger.error("deleteUser: uid {}, {} 删除失败", uid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }else{
                //意外情况
                logger.error("deleteUser: uid {}, {} 修改行数大于1", uid, MESSAGE_DATABASE_ERROR)
                return CommonResult.error(CODE_DATABASE_ERROR, MESSAGE_DATABASE_ERROR)
            }
        }else{
            //查无此人
            logger.info("deleteUser: uid {}, {}", uid, MESSAGE_MESSING)
            return CommonResult.error(CODE_MISSING, MESSAGE_MESSING)
        }
    }
}