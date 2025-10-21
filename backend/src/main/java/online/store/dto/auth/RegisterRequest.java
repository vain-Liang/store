package online.store.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 注册请求 DTO
 */
@Data
public class RegisterRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3 , max = 20, message = "用户名长度必须在3到20个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 16, message = "密码长度必须在6到16个字符之间")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*.!])(?=\\S+$).{6,16}$", message = "密码必须包含数字、大小写字母和至少一个特殊字符(@#$%^&*.!)")
    private  String password;

    /**
     * 电子邮箱
     */
    @Email
    private  String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 账户余额
     */
    // 初始余额可选，默认为0
    private BigDecimal balance = BigDecimal.ZERO;
}
