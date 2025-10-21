package online.store.service.auth;

import online.store.pojo.User;

public interface PermissionService {

    /**
     * 获取当前登录的用户实体
     * @return User 实体，如果未登录则返回 null 或抛出异常
     */
    User getCurrentUser();

    /**
     * 判断当前用户是否为管理员 (ROLE_ADMIN 和 admin)
     * @return boolean
     */
    boolean isAdmin();

    /**
     * 判断当前用户是否为商家 (ROLE_MERCHANT 和 merchant)
     * @return boolean
     */
    boolean isMerchant();

    /**
     * 判断当前用户是否为普通消费者 (ROLE_CONSUMER 和 consumer)
     * @return boolean
     */
    boolean isConsumer();

    /**
     * 校验当前用户是否为指定商品的所有者（商家）
     * @param productId 商品ID
     * @return boolean
     */
    boolean isOwnerOfProduct(Long productId);

    /**
     * 校验当前用户是否为管理员或指定商品的所有者
     * @param productId 商品ID
     * @return boolean
     */
    boolean isAdminOrOwnerOfProduct(Long productId);
}
