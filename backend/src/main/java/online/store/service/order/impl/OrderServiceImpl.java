package online.store.service.order.impl;

import lombok.RequiredArgsConstructor;
import online.store.common.exception.*;
import online.store.common.utils.SnowflakeIdGenerator;
import online.store.dto.order.OrderCreateRequest;
import online.store.enums.OrderStatus;
import online.store.enums.ProductStatus;
import online.store.mapper.OrderItemMapper;
import online.store.mapper.OrderMapper;
import online.store.mapper.ProductMapper;
import online.store.mapper.UserMapper;
import online.store.pojo.OrderItem;
import online.store.pojo.Orders;
import online.store.pojo.Product;
import online.store.pojo.User;
import online.store.service.auth.PermissionService;
import online.store.service.order.OrderService;
import online.store.vo.order.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserMapper userMapper;
    private final PermissionService permissionService;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrderAndPay(OrderCreateRequest orderCreateRequest) {
        // 1. 获取当前用户和目标商品，并施加行级写锁，防止并发问题
        User user = getCurrentUserForUpdate();
        Product product = productMapper.selectByIdForUpdate(orderCreateRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("商品ID: " + orderCreateRequest.getProductId() + " 不存在"));

        // 2. 业务校验
        validatePurchase(user, product, orderCreateRequest.getQuantity());

        // 3. 计算金额
        BigDecimal totalCost = product.getPrice().multiply(BigDecimal.valueOf(orderCreateRequest.getQuantity()));

        // 4. 更新用户余额和商品库存
        user.setBalance(user.getBalance().subtract(totalCost));
        product.setStock(product.getStock() - orderCreateRequest.getQuantity());

        int userUpdateCount = userMapper.updateById(user);
        int productUpdateCount = productMapper.updateById(product);

        // 并发冲突检测：如果更新的行数为0，说明记录已被其他事务修改，抛出异常以回滚
        if (userUpdateCount == 0 || productUpdateCount == 0) {
            throw new RuntimeException("系统繁忙，下单失败，请重试！(并发冲突)");
        }

        // 5. 创建订单和订单项
        LocalDateTime now = LocalDateTime.now();

        // 5.1 创建订单主记录 (Orders)
        Orders newOrder = buildOrder(user.getId(), totalCost, orderCreateRequest, now);
        orderMapper.insert(newOrder);

        // 5.2 创建订单项记录 (OrderItem)
        OrderItem newOrderItem = buildOrderItem(newOrder.getId(), product, orderCreateRequest.getQuantity(), totalCost);
        orderItemMapper.insert(newOrderItem);

        System.out.println(STR."订单创建成功! OrderNo: \{newOrder.getOrderNo()}, User: '\{user.getUsername()}', Product: '\{product.getName()}' x\{orderCreateRequest.getQuantity()}, Cost: \{totalCost}");

        // 6. 构建并返回响应
        return OrderResponse.builder()
                .orderId(newOrder.getId())
                .orderNo(newOrder.getOrderNo())
                .status(newOrder.getStatus())
                .totalAmount(newOrder.getTotalAmount())
                .newBalance(user.getBalance())
                .payTime(newOrder.getPayTime())
                .build();
    }

    /**
     * 校验购买条件：商品状态、库存、用户余额
     */
    private void validatePurchase(User user, Product product, Integer quantity) {
        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new ProductNotForSaleException("商品 '" + product.getName() + "' 当前未出售");
        }
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(String.format("商品 '%s' 库存不足. 当前库存: %d, 需求: %d",
                    product.getName(), product.getStock(), quantity));
        }
        BigDecimal totalCost = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientBalanceException(String.format("账户余额不足. 当前余额: %s, 需要: %s",
                    user.getBalance(), totalCost));
        }
    }

    /**
     * 构造订单对象
     */
    private Orders buildOrder(Long userId, BigDecimal totalAmount, OrderCreateRequest request, LocalDateTime payTime) {
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 在此简单模型中，支付金额等于总金额
        order.setStatus(OrderStatus.PAID); // 因为是立即扣款，所以直接设置为已支付
        order.setAddress(request.getAddress());
        order.setConsignee(request.getConsignee());
        order.setPhone(request.getPhone());
        order.setPayTime(payTime);
        return order;
    }

    /**
     * 构造订单项对象
     */
    private OrderItem buildOrderItem(Long orderId, Product product, Integer quantity, BigDecimal subtotal) {
        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setProductId(product.getId());
        // 关键：保存交易发生时的商品信息快照
        item.setProductName(product.getName());
        item.setProductPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setSubtotal(subtotal);
        return item;
    }

    /**
     * 生成唯一订单号 (例如：时间戳 + 雪花ID后几位)
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String snowflakeSuffix = String.valueOf(snowflakeIdGenerator.nextId()).substring(10); // 取后几位减少长度
        return timestamp + snowflakeSuffix;
    }

    /**
     * 获取当前登录的用户并锁定记录
     */
    private User getCurrentUserForUpdate() {
        String username = permissionService.getCurrentUser().getUsername();
        if (username == null) {
            throw new UserNotFoundException("无法获取当前用户信息,请检查Token是否有效");
        }
        return userMapper.selectByUsernameForUpdate(username)
                .orElseThrow(() -> new UserNotFoundException("用户 '" + username + "' 不存在"));
    }

}
