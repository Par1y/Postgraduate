package cn.sicnu.postgraduate.core.service

import cn.sicnu.postgraduate.core.entity.User
import cn.sicnu.postgraduate.core.exception.CustomException
import cn.sicnu.postgraduate.core.mapper.UserMapper
import cn.sicnu.postgraduate.springsecurity.entity.LoginUser
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.cache.CacheManager
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * 用户服务测试类
 * @author Par1y
 */
@ExtendWith(MockitoExtension::class)
class UserServiceGetUserUnitTest{
    companion object {
        private const val MESSAGE_MISSING: String = "用户不存在"
    }

    @Mock
    private lateinit var mockEnvironment: Environment

    @Mock
    private lateinit var userMapper: UserMapper

    @Mock
    private lateinit var cacheManager: CacheManager

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @InjectMocks
    private lateinit var userService: UserServiceImpl

    //测试用户数据
    private val testUid = "123"
    private val testUidLong = 123L
    private val testUsername = "testUser"
    private val testPassword = "Hello World!"
    private val hashedPassword = "hashed_password"
    private val defaultRole = "ROLE_USER"

    @BeforeEach
    fun setup() {
        reset(userMapper, cacheManager)
    }

    @BeforeEach
    fun isolateEnv() {
        // 强制注入模拟环境
        userService.javaClass.getDeclaredField("environment").apply {
            isAccessible = true
            set(userService, mockEnvironment)
        }
    }


    /**
     * 查询用户测试
     * 正常行为
     */
    @Test
    fun `getUser should return user from mapper`() {
        //  准备测试数据
        val expectedUser = User(testUidLong, testUsername, testPassword)

        //  模拟Mapper行为
        `when`(userMapper.selectById(testUidLong)).thenReturn(expectedUser)

        //  测试
        val actualUser = userService.getUser(testUid)

        //  验证
        assertEquals(expectedUser, actualUser)
        verify(userMapper).selectById(testUidLong)
    }

    /*
     * 查询不到用户
     */
    @Test
    fun `getUser should throw CustomException when user not exists`() {
        //  模拟查询不到
        `when`(userMapper.selectById(testUidLong)).thenReturn(null)

        //  测试
        val exception = assertThrows<CustomException> {
            userService.getUser(testUid)
        }

        //  验证
        assertEquals(MESSAGE_MISSING, exception.message)
        verify(userMapper).selectById(testUidLong)
    }

    /*
     * 用户uid错误
     */
    @Test
    fun `getUser should handle invalid uid format`() {
        //  错误uidStr
        val invalidUid = "abc123"

        //  测试
        val exception = assertThrows<Exception> {
            userService.getUser(invalidUid)
        }

        //  验证
        verify(userMapper, never()).selectById(anyLong())
    }

    /*
     * 空字符串
     */
    @Test
    fun `getUser should handle empty or blank uid`() {
        //  测试：空串
        assertThrows<Exception> {
            userService.getUser("")
        }

        //  测试：空白串
        assertThrows<Exception> {
            userService.getUser("   ")
        }

        //  验证
        verify(userMapper, never()).selectById(anyLong())
   }


    /**
     * 测试用户登录
     * 正常行为
     */
    @Test
    fun `login should return user when authentication is successful`() {
        // 准备数据
        val expectedUser = User(testUidLong, testUsername, hashedPassword)
        val loginUser = LoginUser(expectedUser)
        val authentication = mock(Authentication::class.java)

        // 使用 any() 匹配任意 AuthenticationToken
        `when`(bCryptPasswordEncoder.encode(testPassword)).thenReturn(hashedPassword)
        `when`(authentication.principal).thenReturn(loginUser)
        `when`(authenticationManager.authenticate(any())).thenReturn(authentication)

        // 测试
        val result = userService.login(testUid, testPassword)

        // 验证
        assertEquals(expectedUser, result)
    }

    /*
     *  验证失败
     */
    @Test
    fun `login should throw CustomException when authentication fails`() {
        //  准备数据
        val authenticationToken = UsernamePasswordAuthenticationToken(testUid, testPassword)

        `when`(authenticationManager.authenticate(authenticationToken)).thenReturn(null)

        // 测试&验证
        assertThrows<CustomException> {
            userService.login(testUid, testPassword)
        }
    }

    /*
     * 用户id错误
     */
    @Test
    fun `login should handle invalid uid format`() {
        //  准备数据
        val invalidUidStr = "lbw666"

        //  测试&验证
        assertThrows<Exception> {
            userService.login(invalidUidStr, testPassword)
        }
    }

    /**
     * 测试用户注册
     * 正常行为
     */
    @Test
    fun `newUser should return user when insert success`() {
        `when`(bCryptPasswordEncoder.encode(testPassword)).thenReturn(hashedPassword)
        `when`(userMapper.insert(any<User>())).thenReturn(1)
        // 测试
        val result = userService.newUser(testUsername, testPassword)

        // 验证
        assertNotNull(result.getUid())
        assertEquals(testUsername, result.getUsername())
        assertEquals(hashedPassword, result.getPassword())
        assertEquals(listOf(defaultRole), result.getRoles())
    }

    /*
     *  数据库插入失败
     */
    @Test
    fun `newUser should throw when insert fails`() {
        `when`(bCryptPasswordEncoder.encode(testPassword)).thenReturn(hashedPassword)
        `when`(userMapper.insert(any<User>())).thenReturn(0)

        // 测试&验证
        assertThrows<CustomException> {
            userService.newUser(testUsername, testPassword)
        }
    }

    /**
     * 测试修改用户
     * 正常行为
     */
    @Test
    fun `alterUser should update username when permission granted`() {
        // 认证信息
        val testUser = User(testUidLong, testUsername, testPassword)
        val loginUser = LoginUser(testUser)
        val testAuth = UsernamePasswordAuthenticationToken(
            loginUser, // 模拟正确用户
            null,
            listOf(SimpleGrantedAuthority(defaultRole))
        )
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext().apply {
            authentication = testAuth
        })

        // 准备数据
        val currentUser = User().apply {
            setUid(testUidLong)
            setUsername("oldUsername")
            setPassword("oldPassword")
        }
        val updatedUser = User().apply {
            setUid(testUidLong)
            setUsername(testUsername)
            setPassword(testPassword)
        }

        `when`(userMapper.selectById(testUidLong)).thenReturn(currentUser)
        `when`(userMapper.updateById(any<User>())).thenReturn(1)
        `when`(bCryptPasswordEncoder.encode(testPassword)).thenReturn(hashedPassword)

        // 测试
        val result = userService.alterUser(testUid, testUsername, testPassword)

        // 验证
        assertEquals(testUsername, result.getUsername())
        assertEquals(hashedPassword, result.getPassword())
        verify(userMapper).updateById(currentUser)
    }

    /*
     * 越权修改他人信息
     */
    @Test
    fun `alterUser should throw when permission denied`() {
        // 认证信息
        val testUser = User(456L, testUsername, testPassword)
        val loginUser = LoginUser(testUser)
        val testAuth = UsernamePasswordAuthenticationToken(
            loginUser, // 模拟错误用户
            null,
            listOf(SimpleGrantedAuthority(defaultRole))
        )
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext().apply {
            authentication = testAuth
        })

        //  测试&验证
        assertThrows<CustomException> {
            userService.alterUser(testUid, "newPassword", null)
        }.apply {
            assertEquals("我去黑客来袭", message)
        }
    }

    /*
     *  未进行改动
     */
    @Test
    fun `alterUser should throw when no changes provided`() {
        // 认证信息
        val testUser = User(testUidLong, testUsername, testPassword)
        val loginUser = LoginUser(testUser)
        val testAuth = UsernamePasswordAuthenticationToken(
            loginUser, // 模拟正确用户
            null,
            listOf(SimpleGrantedAuthority(defaultRole))
        )
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext().apply {
            authentication = testAuth
        })

        //测试&验证
        assertThrows<CustomException> {
            userService.alterUser(testUid, null, null)
        }
    }

    /*
     *  改不存在的人（内部错误）
     */
    @Test
    fun `alterUser should throw when user not found`() {
        // 认证信息
        val testUser = User(testUidLong, testUsername, testPassword)
        val loginUser = LoginUser(testUser)
        val testAuth = UsernamePasswordAuthenticationToken(
            loginUser, // 模拟正确用户
            null,
            listOf(SimpleGrantedAuthority(defaultRole))
        )
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext().apply {
            authentication = testAuth
        })

        `when`(userMapper.selectById(testUidLong)).thenReturn(null)

        assertThrows<CustomException> {
            userService.alterUser(testUid, testUsername, null)
        }
    }

    /**
     * 测试删除用户
     * 正常行为
     */
    @Test
    fun `deleteUser should return user when success`() {
        // 认证信息
        val testUser = User(testUidLong, testUsername, testPassword)
        val loginUser = LoginUser(testUser)
        val testAuth = UsernamePasswordAuthenticationToken(
            loginUser, // 模拟正确用户
            null,
            listOf(SimpleGrantedAuthority(defaultRole))
        )
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext().apply {
            authentication = testAuth
        })

        // 准备数据
        val user = User().apply {
            setUid(testUidLong)
            setUsername(testUsername)
            setPassword(testPassword)
        }

        `when`(userMapper.deleteById(any<Long>())).thenReturn(1)

        // 测试
        val result = userService.deleteUser()

        // 验证
        assertEquals(testUser, result)
        verify(userMapper).deleteById(testUidLong)
    }

    /*
     * 删除不存在的人
     */
    @Test
    fun `deleteUser should throw when permission denied`() {
        // 认证信息
        val testUser = User(456L, testUsername, testPassword)
        val loginUser = LoginUser(testUser)
        val testAuth = UsernamePasswordAuthenticationToken(
            loginUser, // 模拟无此用户
            null,
            listOf(SimpleGrantedAuthority(defaultRole))
        )
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext().apply {
            authentication = testAuth
        })

        `when`(userMapper.deleteById(any<Long>())).thenReturn(0)

        // 测试&验证
        assertThrows<CustomException> {
            val result = userService.deleteUser()
        }
    }
}