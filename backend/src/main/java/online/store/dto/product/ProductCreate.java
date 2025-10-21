package online.store.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import online.store.enums.ProductStatus;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

/**
 * 商品创建DTO
 */
@Data
@Schema(name = "ProductCreateDTO", description = "商品创建请求数据")
public class ProductCreate {

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "华为 Mate 60 Pro")
    private String name;

    /**
     * 商品描述
     */
    @Schema(description = "商品详细描述", example = "全新一代旗舰手机，支持卫星通话")
    private String description;

    @URL(message = "图片地址格式不正确")
    private String imageUrl;

    /**
     * 商品售价
     */
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "商品价格", example = "6999.00")
    private BigDecimal price;

    /**
     * 商品成本
     */
    @NotNull(message = "商品成本不能为空")
    @DecimalMin(value = "0.01", message = "成本必须大于0")
    @Schema(description = "商品成本", example = "5999.00")
    private BigDecimal costPrice;

    /**
     * 商品库存数量
     */
    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存必须大于等于0")
    @Schema(description = "商品库存", example = "1")
    private Integer stock = 1;

    /**
     * 商品分类id
     */
    @Schema(description = "商品分类ID", example = "1")
    private Integer categoryId;

    /**
     * 商品状态
     * 1 - 在售
     * 2 - 下架
     * 3 - 缺货
     */
    @NotNull(message = "商品状态不为空")
    @Schema(description = "商品状态, 1-在售, 2-下架, 3-缺货, 默认为1")
    private ProductStatus status = ProductStatus.ON_SALE;

    /* 出售商家ID从当前登录商家用户信息中获取 */
}
