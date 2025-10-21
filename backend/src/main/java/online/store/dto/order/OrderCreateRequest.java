package online.store.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(name = "OrderCreateRequest DTO", description = "订单创建请求数据体")
public class OrderCreateRequest {

    /**
     * 要购买的商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "要购买的商品ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    /**
     * 购买数量, 必须大于0
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    @Schema(description = "购买数量", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer quantity;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    @Size(max = 255, message = "收货地址长度不能超过255个字符")
    @Schema(description = "收货地址", example = "XX省XX市XX区XX街道XX号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50个字符")
    @Schema(description = "收货人姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String consignee;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Schema(description = "联系电话", example = "13888888888", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
}
