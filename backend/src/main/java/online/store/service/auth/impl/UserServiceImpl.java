package online.store.service.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import online.store.common.utils.JwtUtil;
import online.store.common.utils.MaskingUtil;
import online.store.dto.auth.LoginRequest;
import online.store.dto.auth.RefreshTokenRequest;
import online.store.dto.auth.RegisterRequest;
import online.store.enums.UserStatus;
import online.store.mapper.RoleMapper;
import online.store.mapper.UserMapper;
import online.store.mapper.UserRoleMapper;
import online.store.pojo.Role;
import online.store.pojo.User;
import online.store.pojo.UserRole;
import online.store.service.auth.UserService;
import online.store.vo.auth.LoginResponse;
import online.store.vo.auth.RegisterResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现类
 * 提供注册登录业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 密码编码器, 用于加解密密码
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 角色 Mapper, 用于查询角色
     */
    private final RoleMapper roleMapper;

    /**
     * 用户角色 Mapper, 用于操作用户角色关联表
     */
    private final UserRoleMapper userRoleMapper;

    /**
     * JWT 工具类对象, 用于当前用户的 JWT 生成与解析
     */
    private final JwtUtil jwtUtil;


    /**
     * 用户注册
     * @param registerRequest 注册请求数据对象, 包含用户名、密码、邮箱、手机号、余额
     * @return registerResponse 注册响应数据体, 包含用户ID、用户名、脱敏邮箱、脱敏手机号
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        // 检查用户名是否为空
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        // 检查用户名是否唯一
        if (this.count(new QueryWrapper<User>().eq("username", registerRequest.getUsername())) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // (可选) 检查邮箱是否唯一
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().isBlank() && this.count(new QueryWrapper<User>().eq("email", registerRequest.getEmail())) > 0) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        /* 设置User对象各属性的值 */
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail() == null ? "" : registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone() == null ? "" : registerRequest.getPhone());
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(UserStatus.NORMAL);

        this.save(user); // 保存, 自动填充createdTime, updatedTime 与 id

        /* 设置用户角色, 默认注册用户为消费者 (普通用户) */
        Role role = roleMapper.selectByRoleName("consumer");
        if (role == null) {
            throw new IllegalStateException("默认角色 'consumer' 不存在");
        } else {
            UserRole ur = new UserRole();
            ur.setUserId(user.getId());
            ur.setRoleId(role.getId());
            userRoleMapper.insert(ur);
        }

        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                MaskingUtil.maskEmail(user.getEmail()),
                MaskingUtil.maskPhone(user.getPhone())
        );
    }

    /**
     * 用户登录
     * @param loginRequest 登录请求数据体, 包含用户名和密码
     * @return loginResponse 登录响应数据, 包含用户脱敏信息
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User user = this.getOne(new QueryWrapper<User>().eq("username", loginRequest.getUsername()));

        if (user == null) {
            throw new IllegalArgumentException("用户不存在, 请注册");
        }
        if ((!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) || (Objects.equals(loginRequest.getPassword(), user.getUsername()))) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new IllegalArgumentException("用户已被冻结, 请联系管理员");
        }

        // 查询用户角色
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        user.setRoles(roles);

        // 生成 access token 和 refresh token
        String roleName = roles.isEmpty() ? "consumer" : roles.getFirst().getRoleName();
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), roleName);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 构造返回 LoginResponse VO 对象
        LoginResponse vo = new LoginResponse();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(MaskingUtil.maskEmail(user.getEmail()));
        vo.setPhone(MaskingUtil.maskPhone(user.getPhone()));
        vo.setBalance(user.getBalance());
        vo.setRoles(user.getRoles());
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        return vo;
    }

    /**
     * 刷新 Token
     * @param refreshTokenRequest 刷新令牌请求数据体, 包含旧的 Access Token(可选) 和 Refresh Token(必选)
     * @return 包含新的 Access Token 的用户信息
     */
    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (jwtUtil.isExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh token 已过期，请重新登录");
        }

        Claims claims = jwtUtil.parseToken(refreshToken);
        String username = claims.getSubject();
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 重新生成 Access Token
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        String roleName = roles.isEmpty() ? "consumer" : roles.getFirst().getRoleName();
        String newAccessToken = jwtUtil.generateAccessToken(username, roleName);

        // 构造返回对象
        LoginResponse vo = new LoginResponse();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(refreshToken); // refresh token 不变
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(MaskingUtil.maskEmail(user.getEmail()));
        vo.setPhone(MaskingUtil.maskPhone(user.getPhone()));
        vo.setBalance(user.getBalance());
        vo.setRoles(roles);
        return vo;
    }
}
