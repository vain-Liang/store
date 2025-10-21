package online.store.vo.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "RegisterResponse", description = "用户注册成功返回信息")
public class RegisterResponse {

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID", example = "100230928123")
    @JsonSerialize(using = ToStringSerializer.class) // 避免前端 long 类型导致前端Javascript精度丢失问题
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "jason")
    private String username;

    /**
     * 脱敏后的邮箱
     */
    @Schema(description = "邮箱（脱敏）", example = "new****@example.com")
    private String email;

    /**
     * 脱敏后的手机号
     */
    @Schema(description = "手机号（脱敏）", example = "138****1234")
    private String phone;
}
