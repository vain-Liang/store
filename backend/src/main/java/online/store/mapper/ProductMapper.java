package online.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.store.pojo.Product;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 商品Mapper接口
 */
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT * FROM product WHERE id = #{id} FOR UPDATE")
    Optional<Product> selectByIdForUpdate(Long id);
}
