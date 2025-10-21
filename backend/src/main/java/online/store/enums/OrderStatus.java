package online.store.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 订单状态枚举
 * 1-待支付, 2-已支付, 3-已发货, 4-已完成, 5-已取消, 6-退款中, 7-已退款
 */
@Getter
public enum OrderStatus {
    PENDING(1, "待支付"),
    PAID(2, "已支付"),
    SHIPPED(3, "已发货"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    REFUNDING(6, "退款中"),
    REFUNDED(7, "已退款");

    /**
     * MyBatis-Plus 标记数据库存储的枚举值为code
     */
    @EnumValue
    private final int code;

    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
