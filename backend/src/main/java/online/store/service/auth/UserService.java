package online.store.service.auth;

import com.baomidou.mybatisplus.extension.service.IService;
import online.store.dto.auth.LoginRequest;
import online.store.dto.auth.RefreshTokenRequest;
import online.store.dto.auth.RegisterRequest;
import online.store.pojo.User;
import online.store.vo.auth.LoginResponse;
import online.store.vo.auth.RegisterResponse;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param registerRequest 注册请求数据, 用户名、密码、邮箱、手机号
     * @return 注册响应数据体, 脱敏后的数据
     */
    RegisterResponse register(RegisterRequest registerRequest);

    /**
     * 用户登录
     * @param loginRequest 登录请求数据, 用户名和密码
     * @return 登录响应数据, 脱敏数据
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 刷新 Access Token 令牌
     * @param refreshTokenRequest 刷新令牌请求数据体, 包含旧的 Access Token(可选) 和 Refresh Token(必选)
     * @return 包含新 Access Token 的 LoginResponse 对象
     */
    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
