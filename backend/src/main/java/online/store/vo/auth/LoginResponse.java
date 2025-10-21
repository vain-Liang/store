package online.store.vo.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import online.store.pojo.Role;

import java.math.BigDecimal;
import java.util.List;

/**
 * 登录响应VO, 登录成功返回信息
 * 包含用户脱敏处理后的基本信息 + Token
 */
@Data
@Schema(name = "LoginResponse", description = "登录返回信息，包含脱敏用户信息与 Token")
public class LoginResponse {

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID", example = "1001")
    @JsonSerialize(using = ToStringSerializer.class) // 避免前端精度丢失
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "jason")
    private  String username;

    /**
     * 脱敏后的邮箱
     */
    @Schema(description = "邮箱（脱敏）", example = "tes*@example.com")
    private String email;

    /**
     * 脱敏后的手机号
     */
    @Schema(description = "手机号（脱敏）", example = "138****1234")
    private String phone;

    /**
     * 账户余额
     */
    @Schema(description = "账户余额", example = "100.20")
    private BigDecimal balance;

    /**
     * 用户角色集合
     */
    @Schema(description = "用户角色列表")
    private List<Role> roles;

    /**
     * JWT Access Token
     */
    @Schema(description = "JWT 访问令牌 (Access Token)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI...")
    private String accessToken;

    /**
     * JWT Refresh Token
     */
    @Schema(description = "JWT 刷新令牌 (Refresh Token)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI...")
    private String refreshToken;

    /**
     * Token类型, 默认为 Bearer
     */
    @Schema(description = "令牌类型, 默认为 Bearer", example = "Bearer")
    private String tokenType = "Bearer";
}
