package online.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.store.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 用户 Mapper 层接口
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return User 实体对象
     */
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    /**
     * 数据行锁, 确保事务一致
     * @param username 正在修改信息的用户名
     * @return Optional对象
     */
    @Select("SELECT * FROM user WHERE username = #{username} FOR UPDATE")
    Optional<User> selectByUsernameForUpdate(String username);

}
