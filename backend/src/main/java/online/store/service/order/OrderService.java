package online.store.service.order;

import online.store.dto.order.OrderCreateRequest;
import online.store.vo.order.OrderResponse;

/**
 * 订单服务接口
 * <p>定义订单创建、查询、状态变更等核心业务</p>
 */
public interface OrderService {

    /**
     * 创建订单并完成支付。
     * <p>
     * 这是一个核心的事务方法，它会原子性地完成以下操作：
     * 1. 悲观锁锁定用户和商品记录，防止并发问题。
     * 2. 校验商品状态、库存及用户余额。
     * 3. 扣减用户余额和商品库存。
     * 4. 创建订单（Orders）和订单项（OrderItem）记录。
     * 5. 返回创建成功的订单信息。
     * </p>
     *
     * @param createRequest 包含商品、数量及收货信息的订单创建请求
     * @return 包含订单ID、订单号和支付状态的响应对象
     * @throws online.store.exception.UserNotFoundException      当前登录用户不存在
     * @throws online.store.exception.ProductNotFoundException     商品不存在
     * @throws online.store.exception.ProductNotForSaleException   商品未处于出售状态
     * @throws online.store.exception.InsufficientStockException   商品库存不足
     * @throws online.store.exception.InsufficientBalanceException 用户余额不足
     * @throws RuntimeException                                   发生并发更新或其他未知异常
     */
    OrderResponse createOrderAndPay(OrderCreateRequest createRequest);
}