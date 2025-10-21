package online.store.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import online.store.enums.SelectedStatus;

import java.io.Serializable;

/**
 * 购物车实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("`cart_item`")
public class CartItem implements Serializable {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品购买数量
     */
    private Integer quantity;

    /**
     * 是否选中:
     * 1 - 是
     * 0 - 否
     */
    private SelectedStatus selected;

}
