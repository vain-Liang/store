package online.store.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.store.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    @Schema(description = "订单ID", example = "1234567890")
    @JsonSerialize(using = ToStringSerializer.class) // 避免前端 long 类型导致前端Javascript精度丢失问题
    private Long orderId;

    @Schema(description = "订单号", example = "20231027123456789")
    private String orderNo;

    @Schema(description = "订单状态", example = "PAID")
    private OrderStatus status;

    @Schema(description = "订单总金额", example = "9999.00")
    private BigDecimal totalAmount;

    @Schema(description = "交易后账户最新余额", example = "8002.00")
    private BigDecimal newBalance;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;
}
