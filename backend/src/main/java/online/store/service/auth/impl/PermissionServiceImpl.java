package online.store.service.auth.impl;

import jakarta.annotation.Resource;
import online.store.mapper.ProductMapper;
import online.store.mapper.RoleMapper;
import online.store.mapper.UserMapper;
import online.store.pojo.Product;
import online.store.pojo.Role;
import online.store.pojo.User;
import online.store.service.auth.PermissionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ps")
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    /**
     * 获取当前登录用户信息
     * @return 当前登录用户对象
     */
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // TODO: 根据业务需求抛出未认证异常
        }
        // 获取当前用户名
        String currentUsername = authentication.getName();
        // 从数据库获取完整的 User 对象
        User currrentUser = userMapper.selectByUsername(currentUsername);
        // 查询当前用户角色
        List<Role> roles = roleMapper.selectRolesByUserId(currrentUser.getId());
        // 查询到的角色列表保存到属性
        currrentUser.setRoles(roles);

        return currrentUser;
    }

    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + roleName.toUpperCase()));
    }

    @Override
    public boolean isAdmin() {
        return hasRole("admin");
    }

    @Override
    public boolean isMerchant() {
        return hasRole("merchant");
    }

    @Override
    public boolean isConsumer() {
        return hasRole("consumer");
    }

    @Override
    public boolean isOwnerOfProduct(Long productId) {
        User currentUser = getCurrentUser();
        if (currentUser == null || !isMerchant()) {
            return false;
        }
        Product product = productMapper.selectById(productId);
        // 确保商品存在且其商家ID与当前登录用户ID匹配
        return product != null && product.getMerchantId().equals(currentUser.getId());
    }

    @Override
    public boolean isAdminOrOwnerOfProduct(Long productId) {
        return isAdmin() || isOwnerOfProduct(productId);
    }
}
