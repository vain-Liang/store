package online.store.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import online.store.dto.product.ProductCreate;
import online.store.dto.product.ProductUpdate;
import online.store.enums.ProductStatus;
import online.store.mapper.ProductMapper;
import online.store.pojo.Product;
import online.store.pojo.User;
import online.store.service.auth.PermissionService;
import online.store.service.product.ProductService;
import online.store.vo.product.ProductDetail;
import online.store.vo.product.ProductPublic;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private PermissionService permissionService;

    /**
     * 创建商品
     * @param productCreate 商品创建请求数据对象
     * @return ProductDetail 对象, 包含商品全部信息
     */
    @Override
    public ProductDetail createProduct(ProductCreate productCreate) {
        User currentUser = permissionService.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("用户未登录，无权创建商品");
        }

        Product product = new Product();
        BeanUtils.copyProperties(productCreate, product);
        // 如果是商家创建，设置商家ID
        if(permissionService.isMerchant()){
            product.setMerchantId(currentUser.getId());
        }
        // 如果是管理员创建，merchantId 可以为 null 或指定一个已存在的商家ID
        this.save(product);

        ProductDetail vo = new ProductDetail();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }

    /**
     * 更新商品
     * @param productId 需要更新的商品的Id
     * @param productUpdate 更新商品VO数据体
     * @return 商品详细信息
     */
    @Override
    public ProductDetail updateProduct(Long productId, ProductUpdate productUpdate) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        BeanUtils.copyProperties(productUpdate, product);
        this.updateById(product);

        ProductDetail vo = new ProductDetail();
        BeanUtils.copyProperties(this.getById(productId), vo); // 重新查询以获取最新数据
        return vo;
    }

    /**
     * 删除商品
     * @param productId 需要删除的商品的ID
     */
    @Override
    public void deleteProduct(Long productId) {
        // 删除操作由 Controller 层的 @PreAuthorize 控制权限
        if (!this.removeById(productId)) {
            throw new IllegalArgumentException("商品不存在或删除失败");
        }
    }

    /**
     * 根据商品ID查询信息
     * @param productId 查询信息的商品ID
     * @return Object 对象
     */
    @Override
    public Object getProductById(Long productId) {
        Product product = this.getById(productId);
        if (product == null) {
            return null;
        }

        // 管理员或者商品的所有者
        if (permissionService.isAdminOrOwnerOfProduct(productId)) {
            ProductDetail vo = new ProductDetail();
            BeanUtils.copyProperties(product, vo);
            return vo;
        } else {
            // 其他情况（包括未登录用户、普通消费者、非商品所有者的商家）只返回公开信息
            ProductPublic vo = new ProductPublic();
            BeanUtils.copyProperties(product, vo);
            return vo;
        }
    }

    /**
     * 在售商品查询结果分页
     * @param pageNum 当前页数
     * @param pageSize 分页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    @Override
    public IPage<ProductPublic> listProductsPublic(int pageNum, int pageSize, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();

        // 只查询在售商品
        queryWrapper.eq("status", ProductStatus.ON_SALE);

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like("name", keyword).or().like("description", keyword));
        }

        // 未登录用户只能看到前100个商品
        if (permissionService.getCurrentUser() == null) {
            // 限制查询范围, example: 限制总数
            queryWrapper.last("LIMIT 100");
            // 注意: MyBatis-Plus的分页会先count, 再limit。直接用last("LIMIT 100") 可能导致count不准, 在Controller层限制pageSize
            // TODO: 自定义一个分页查询
            if(pageNum * pageSize > 100) {
                return new Page<ProductPublic>(pageNum, pageSize).setRecords(java.util.Collections.emptyList()).setTotal(100);
            }
        }

        Page<Product> productPage = this.page(page, queryWrapper);

        // 转换成VO
        return productPage.convert(product -> {
            ProductPublic vo = new ProductPublic();
            BeanUtils.copyProperties(product, vo);
            return vo;
        });
    }
}
