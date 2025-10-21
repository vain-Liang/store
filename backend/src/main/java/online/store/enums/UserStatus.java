package online.store.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 用户状态枚举
 * 1 - 正常
 * 0 - 冻结
 */
@Getter
public enum UserStatus {
    NORMAL(1, "正常"),
    FROZEN(0, "冻结");

    /**
     * MyBatis-Plus 枚举映射, 标记数据库存储的值为code
     */
    @EnumValue
    private final int code;

    private final String desc;

    UserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
