package online.store.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import online.store.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 含有商品详细信息(包括成本、创建更新时间等敏感信息)
 * 返回给有权限的用户(商家、管理员)
 */
@Data
@Schema(name = "ProductDetailVO", description = "含商品敏感信息数据对象")
public class ProductDetail {

    @Schema(description = "商品ID", example = "10938823612")
    @JsonSerialize(using = ToStringSerializer.class) // 避免前端 long 类型导致前端Javascript精度丢失问题
    private Long id;

    @Schema(description = "商品名称", example = "华为 Mate 60 Pro")
    private String name;

    @Schema(description = "商品详细描述", example = "全新一代旗舰手机，支持卫星通话")
    private String description;

    @Schema(description = "商品图片URL", example = "https://example.com/images/mate60pro.jpg")
    private String imageUrl;

    @Schema(description = "商品价格", example = "7999.00")
    private BigDecimal price;

    @Schema(description = "商品成本", example = "5999.00")
    private BigDecimal costPrice;

    @Schema(description = "商品库存", example = "1")
    private Integer stock;

    @Schema(description = "商品分类ID", example = "1")
    private Integer categoryId;

    @Schema(description = "商品状态, 1-在售, 2-下架, 3-缺货, 默认为1")
    private ProductStatus status;

    @Schema(description = "商品出售商家ID")
    @JsonSerialize(using = ToStringSerializer.class) // 避免前端 long 类型导致前端Javascript精度丢失问题
    private Long merchantId;

    @Schema(description = "商品创建(上架)时间")
    private LocalDateTime createdTime;

    @Schema(description = "商品更新时间")
    private LocalDateTime updatedTime;
}
