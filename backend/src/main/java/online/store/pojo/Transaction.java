package online.store.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.store.enums.TransactionStatus;
import online.store.enums.TransactionType;

import java.math.BigDecimal;

/**
 * 支付交易实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`transaction`")
public class Transaction extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联订单ID
     * 充值和退款的ID单独生成, 不进行关联
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 类型: 1-充值, 2-消费, 3-退款
     */
    private TransactionType type;

    /**
     * 状态: 1-成功, 2-失败, 3-处理中
     */
    private TransactionStatus status;

    /**
     * 第三方交易号
     */
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 交易备注
     */
    private String remark;

}
