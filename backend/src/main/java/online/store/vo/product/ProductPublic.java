package online.store.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import online.store.enums.ProductStatus;

import java.math.BigDecimal;

/**
 * 不含成本的商品信息
 */
@Data
@Schema(name = "ProductPublicVO", description = "商品公开信息数据对象")
public class ProductPublic {

    @Schema(description = "商品ID", example = "19018317219")
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

    @Schema(description = "商品库存", example = "1")
    private Integer stock;

    @Schema(description = "商品分类ID", example = "1")
    private Integer categoryId;

    @Schema(description = "商品状态, 1-在售, 2-下架, 3-缺货, 默认为1")
    private ProductStatus status;

    // TODO: 按商家Id筛选商品
    // private Long merchantID; //商家ID, 按商家筛选商品
}
