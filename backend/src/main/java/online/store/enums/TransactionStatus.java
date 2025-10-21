package online.store.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 交易状态枚举类
 * 1-成功, 2-失败, 3-处理中
 */
@Getter
public enum TransactionStatus {

    SUCCESS(1, "成功"),
    FAILED(2, "失败"),
    PROCESSING(3, "处理中");

    /**
     * MyBatis-Plus 自动映射, 标记数据库存储的值为code
     */
    @EnumValue
    private final int code;

    private final String desc;

    TransactionStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
