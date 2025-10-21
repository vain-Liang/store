package online.store.service.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import online.store.dto.product.ProductCreate;
import online.store.dto.product.ProductUpdate;
import online.store.pojo.Product;
import online.store.vo.product.ProductDetail;
import online.store.vo.product.ProductPublic;

public interface ProductService extends IService<Product> {

    ProductDetail createProduct(ProductCreate productCreate);

    ProductDetail updateProduct(Long productId, ProductUpdate productUpdate);

    void  deleteProduct(Long productId);

    Object getProductById(Long productId);

    IPage<ProductPublic> listProductsPublic(int pageNum, int pageSize, String keyword);
}
