package online.store.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 商品状态枚举
 * 1 - 在售
 * 2 - 下架
 * 3 - 缺货
 */
@Getter
public enum ProductStatus {
    ON_SALE(1, "在售"),
    OFF_SALE(2, "下架"),
    OUT_OF_STOCK(3, "缺货");

    /**
     * MyBatis-Plus 枚举值映射，数据库中存储值为code
     */
    @EnumValue
    private final int code;

    private final String desc;

    ProductStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
