package online.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.store.pojo.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色名称查询角色
     * @param roleName 角色名称
     * @return Role 角色对象
     */
    @Select("SELECT * FROM role WHERE role_name = #{roleName}")
    Role selectByRoleName(@Param("roleName") String roleName);

    /**
     * 根据用户ID查询该用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM role r " +
            "JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

}
