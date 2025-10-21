package online.store.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 交易类型枚举
 * 1-充值, 2-消费, 3-退款
 */
@Getter
public enum TransactionType {
    RECHARGE(1, "充值"),
    PURCHASE(2, "消费"),
    REFUND(3, "退款");

    /**
     * MyBatis-Plus 枚举值注解，标识该字段为枚举在数据库中的存储值为code
     */
    @EnumValue
    private final int code;

    private final String desc;

    TransactionType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
