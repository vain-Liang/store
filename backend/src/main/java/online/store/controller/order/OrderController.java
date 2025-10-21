package online.store.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.store.dto.order.OrderCreateRequest;
import online.store.pojo.Result;
import online.store.service.order.OrderService;
import online.store.vo.order.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理", description = "提供订单的创建、查询等接口")
@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单并支付
     *
     * @param createRequest 包含商品ID、数量和收货信息的请求体
     * @return 包含订单创建结果的统一响应体
     */
    @PostMapping
    @PreAuthorize("hasRole('CONSUMER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建订单并支付", description = "消费者使用账户余额购买商品并生成订单。这是一个原子操作，会同时完成扣款、减库存和订单创建。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "订单创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数无效或业务逻辑错误 (如库存不足、余额不足)"),
            @ApiResponse(responseCode = "403", description = "权限不足, 只有消费者可以下单"),
            @ApiResponse(responseCode = "404", description = "指定商品或当前用户不存在")
    })
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest createRequest) {
        OrderResponse response = orderService.createOrderAndPay(createRequest);
        return Result.success(response);
    }

    // TODO: 在此可以继续添加其他订单相关接口:
    // 1. GET /api/orders/{id} - 查询订单详情
    // 2. GET /api/orders - 分页查询我的订单列表
    // 3. PUT /api/orders/{id}/cancel - 取消订单
}
