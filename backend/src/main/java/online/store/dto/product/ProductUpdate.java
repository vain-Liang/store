package online.store.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import online.store.enums.ProductStatus;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

/**
 * 商品更新DTO
 */
@Data
@Schema(name = "ProductUpdateDTO", description = "商品更新请求数据")
public class ProductUpdate {

    @Schema(description = "商品名称", example = "华为 Mate 60 Pro 16GB+512GB")
    private String name;

    @Schema(description = "商品详细描述", example = "全新一代旗舰手机，支持卫星通话，更大内存容量")
    private String description;

    @URL(message = "图片地址格式不正确")
    private String imageUrl;

    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "商品价格", example = "7299.00")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "成本必须大于0")
    @Schema(description = "商品成本", example = "5999.00")
    private BigDecimal costPrice;

    @Min(value = 0, message = "库存不能为负数")
    @Schema(description = "库存数量", example = "50")
    private Integer stock;

    @Schema(description = "商品分类ID", example = "1")
    private Integer categoryId;

    @Schema(description = "商品状态", example = "ON_SALE")
    private ProductStatus status;
}
