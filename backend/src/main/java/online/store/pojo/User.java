package online.store.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import online.store.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户实体类
 * <p>继承 BaseEntity 基类, 含公共字段 id,created_time,updated_time</p>
 * <p>实现 Spring Security 的 UserDetails接口, 用于后续权限控制</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`user`")
@Schema(name = "User", description = "用户实体类, 包含基本信息和角色")
public class User extends BaseEntity implements UserDetails {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Schema(description = "用户名", example = "jason")
    private  String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 16, message = "密码长度必须在6-16个字符之间")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*.!])(?=\\S+$).{6,16}$", message = "密码必须包含数字、大小写字母和至少一个特殊字符(@#$%^&*.!), 且不能包含空格")
    @Schema(description = "加密存储的用户密码", example = "P@ssw0rd!")
    private  String password;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "用户邮箱", example = "test@example.com")
    private String email;

    /**
     * 用户电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "11位手机号", example = "13909862791")
    private String phone;

    /**
     * 账户余额
     */
    @NotNull(message = "余额不能为空")
    @Schema(description = "账户余额", example = "100.20")
    private BigDecimal balance;

    /**
     * 账号状态
     * 1 - 正常
     * 0 - 冻结或禁用
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "账号状态, 1-正常, 0-冻结或禁用, 默认1")
    private UserStatus status;

    /**
     * 上一次登录时间
     */
    @TableField(value = "last_login")
    @Schema(description = "上一次登录时间")
    private LocalDateTime lastLogin;

    /**
     * 用户角色列表 (非数据库字段)
     */
    @TableField(exist = false)
    @Schema(description = "用户角色列表")
    private List<Role> roles;

    /**
     * 角色校验
     * @return 角色集合
     */
    @Override
    @Schema(hidden = true) // 在Swagger文档中隐藏
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roles == null || this.roles.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_CONSUMER")); // 返回默认角色, List.of()返回空列表
        }
        // 将 Role 集合转换为 GrantedAuthority 集合
        // Spring Security 默认需要 "ROLE_" 前缀
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase())) //角色名大写转换
                .collect(Collectors.toList());
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonExpired() {
        return true; // 账户未过期
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonLocked() {
        return this.status == UserStatus.NORMAL; // 账户未锁定 (根据status字段判断)
    }

    @Override
    @Schema(hidden = true)
    public boolean isCredentialsNonExpired() {
        return true; // 凭证未过期
    }

    @Override
    @Schema(hidden = true)
    public boolean isEnabled() {
        return this.status == UserStatus.NORMAL; // 账户已启用 (根据status字段判断)
    }
}
