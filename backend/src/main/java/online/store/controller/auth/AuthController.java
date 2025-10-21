package online.store.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import online.store.common.utils.JwtUtil;
import online.store.dto.auth.LoginRequest;
import online.store.dto.auth.RefreshTokenRequest;
import online.store.dto.auth.RegisterRequest;
import online.store.service.auth.UserService;
import online.store.pojo.Result;
import online.store.service.common.TokenBlacklistService;
import online.store.vo.auth.LoginResponse;
import online.store.vo.auth.RegisterResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

/**
 * 认证控制器
 * 提供注册、登录、获取当前用户信息等接口
 */
@Tag(name = "认证", description = "用户注册、登录、退出登录、Token刷新及获取当前用户信息等接口")
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private TokenBlacklistService tokenBlacklistService;

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 用户注册接口
     * @param req 注册请求体, 包含用户名、密码、邮箱、手机号
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口, 提供用户名、密码、邮箱、手机号")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误, 如用户名或邮箱已存在")
    })
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        RegisterResponse registerResponse = userService.register(req);
        return Result.success(registerResponse);
    }

    /**
     * 用户登录接口
     * @param req 登录请求体, 包含用户名和密码
     * @return 登录用户数据, 包含用户信息和JWT令牌
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口, 提供用户名和密码, 返回登录成功的包含 Access Token 和 Refresh Token 的用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "认证失败, 用户名或密码错误")
    })
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse loginResponse = userService.login(req);
        return Result.success(loginResponse);
    }

    /**
     * 用户退出登录接口
     * @return 退出登录结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户退出登录", description = "用户退出登录, 当前 Token 将失效", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "退出登录成功", content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token无效或过期")
    })
    public Result<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 解析Token获取剩余过期时间
            try {
                Instant expiration = jwtUtil.parseToken(token).getExpiration().toInstant();
                long remainingTime = Duration.between(Instant.now(), expiration).getSeconds();

                // 将Token加入黑名单
                if (remainingTime > 0) {
                    tokenBlacklistService.add(token, remainingTime);
                }

            } catch (AuthenticationException e) {
                // 如果Token解析失败（例如已过期），则无需加入黑名单，直接认为退出成功
                // log.warn("Invalid token on logout: {}", e.getMessage());
            }
        }
        return Result.success("退出登录成功");
    }

    /**
     * 获取当前认证用户信息接口
     * @param authentication 当前认证对象, 由Spring Security注入
     * @return 当前用户信息, 如果未认证则返回错误
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "根据Token获取当前认证的用户信息", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证, Token无效或过期")
    })
    // TODO: 返回用户信息而非用户名
    public Result<?> me(Authentication authentication) {

        if (authentication == null) {
            return Result.error("未认证");
        }
        return Result.success(authentication.getName());
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用 Refresh Token 获取新的 Access Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token刷新成功", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Refresh Token 无效或已过期")
    })
    public Result<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        // 虽然Token刷新通常发生在Access Token已过期后, 确保安全, 把旧的Access Token也加入黑名单
        if (refreshTokenRequest.getAccessToken() != null) {
            Instant expiration = jwtUtil.parseToken(refreshTokenRequest.getAccessToken()).getExpiration().toInstant();
            long remainingTime = Duration.between(Instant.now(), expiration).getSeconds();
            if (remainingTime > 0) {
                tokenBlacklistService.add(refreshTokenRequest.getAccessToken(), remainingTime);
            }
        }
        LoginResponse loginResponse = userService.refreshToken(refreshTokenRequest);
        return Result.success(loginResponse);
    }
}
