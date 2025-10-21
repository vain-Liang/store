package online.store.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
@Schema(name = "LoginRequest", description = "登录请求参数")
public class LoginRequest {

    /**
     * 登录用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "jason")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "6-16位密码", example = "P@ssw0rd!")
    private  String password;
}
