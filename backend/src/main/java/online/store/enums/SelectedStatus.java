package online.store.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 购物车商品选中状态枚举
 * 1 - 选中
 * 0 - 未选中
 */
@Getter
public enum SelectedStatus {
    SELECTED(1, "选中"),
    UNSELECTED(0, "未选中");

    /**
     * MyBatis-Plus 枚举值映射，数据库中存储值为code
     */
    @EnumValue
    private final int code;

    private final String desc;

    SelectedStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
