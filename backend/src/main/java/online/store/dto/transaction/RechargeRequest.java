package online.store.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "RechargeRequestDTO", description = "充值请求DTO")
public class RechargeRequest {

    /**
     * 充值金额, 单次充值不能低于1元
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "1.00", message = "充值金额必须大于1元")
    @Schema(description = "充值金额", example = "100.01", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;
}
