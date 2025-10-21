package online.store.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Access Token 请求体
 */
@Data
@Schema(name = "RefreshTokenRequest", description = "刷新Token请求体")
public class RefreshTokenRequest {

    /**
     * 旧的 Access Token (可选, 当手动刷新而非过期自动刷新时需要)
     */
    @Schema(description = "Access Token, 当手动刷新时需要", requiredMode = Schema.RequiredMode.AUTO, example = "eyJhbGciOiJI...")
    private  String accessToken;

    /**
     * 用于刷新 Access Token 的 Refresh Token
     */
    @NotBlank(message = "Refresh Token 不为空")
    @Schema(description = "用于刷新Access Token的Refresh Token", requiredMode = Schema.RequiredMode.REQUIRED, example = "eyJhbGciOiJI...")
    private String refreshToken;
}
