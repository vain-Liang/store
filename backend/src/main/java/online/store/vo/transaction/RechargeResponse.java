package online.store.vo.transaction;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值成功后的响应数据视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RechargeResponse VO", description = "账户充值结果响应数据体")
public class RechargeResponse {

    @Schema(description = "充值流水Id")
    @JsonSerialize(using = ToStringSerializer.class) // 避免前端 long 类型导致前端Javascript精度丢失问题
    private long transactionId;

    /**
     * 充值金额
     */
    @Schema(description = "本次充值金额", example = "100.80")
    private BigDecimal amount;

    /**
     * 充值后的账户余额
     */
    @Schema(description = "充值后账户余额", example = "101.80")
    private BigDecimal newBalance;

    /**
     * 充值交易完成时间
     */
    @Schema(description = "充值完成时间")
    private LocalDateTime transactionTime;
}
