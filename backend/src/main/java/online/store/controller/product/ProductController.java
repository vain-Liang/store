package online.store.controller.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import online.store.dto.product.ProductCreate;
import online.store.dto.product.ProductUpdate;
import online.store.pojo.Result;
import online.store.service.product.ProductService;
import online.store.vo.product.ProductPublic;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品", description = "商品添加、删除、更改、查询接口")
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 添加商品
     * @param productCreate 添加商品数据传输对象
     * @return 添加商品结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANT')")
    @Operation(summary = "创建商品", description = "仅管理员和商家可创建", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "商品添加成功"),
            @ApiResponse(responseCode = "403", description = "用户无权进行商品添加")
    })
    public Result<?> createProduct(@Valid @RequestBody ProductCreate productCreate) {
        return Result.success(productService.createProduct(productCreate));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ps.isAdminOrOwnerOfProduct(#id)")
    @Operation(summary = "更新商品", description = "管理员可更新任意商品，商家只能更新自己的商品", security = @SecurityRequirement(name = "bearerAuth"))
    public Result<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdate productUpdate) {
        return Result.success(productService.updateProduct(id, productUpdate));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除商品", description = "仅管理员可删除", security = @SecurityRequirement(name = "bearerAuth"))
    public Result<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "公共接口，但登录用户（商家/管理员）可看到更多信息")
    public ResponseEntity<Result<Object>> getProductById(@PathVariable Long id) {
        Object productVO = productService.getProductById(id);
        if (productVO == null) {
            return ResponseEntity.status(404).body(Result.error("商品未找到"));
        }
        return ResponseEntity.ok(Result.success(productVO));
    }

    @GetMapping
    @Operation(summary = "分页查询商品", description = "公共接口，未登录用户有查询限制。")
    public Result<IPage<ProductPublic>> listProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword) {

        // 对未登录用户进行pageSize限制
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication == null || authentication.getPrincipal().equals("anonymousUser")) && pageSize > 20) {
            pageSize = 20; // 未登录用户每页最多看20条
        }

        IPage<ProductPublic> page = productService.listProductsPublic(pageNum, pageSize, keyword);
        return Result.success(page);
    }
}
