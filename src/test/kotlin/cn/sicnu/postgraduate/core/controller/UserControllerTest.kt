package cn.sicnu.postgraduate.core.controller

import cn.sicnu.postgraduate.core.controllPlaner.UserController
import cn.sicnu.postgraduate.core.service.UserService
import org.junit.jupiter.api.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.test.web.servlet.MockMvc

/**
 * 用户接口测试类
 * @author Par1y
 */
@Disabled("待实现")
@WebMvcTest(UserController::class)
class UserControllerTest {
    companion object {
        private const val MESSAGE_WRONG_PARAM: String = "请求参数错误"
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var mockEnvironment: Environment

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var userController: UserController

    @BeforeEach
    fun isolateEnv() {
        // 强制注入模拟环境
        userService.javaClass.getDeclaredField("environment").apply {
            isAccessible = true
            set(userService, mockEnvironment)
        }
    }

    /**
     * 测试查询用户
     * 正常行为
     */
    @Test
    fun `GET user by uid should return 200 when user exists`() {
        TODO("接口测试")
    }

    @Test
    fun `GET user should return 404 when user not found`() {
        TODO("接口测试")
    }

    @Test
    fun `POST login should return token when credentials valid`() {
        TODO("接口测试")
    }

    @Test
    fun `POST login should return 401 when password wrong`() {
        TODO("接口测试")
    }

    @Test
    fun `POST register should validate username format`() {
        TODO("接口测试")
    }

    @Test
    fun `PUT alter user should return 403 when no permission`() {
        TODO("接口测试")
    }

    @Test
    fun `DELETE user should return 204 by admin`() {
        TODO("接口测试")
    }
}