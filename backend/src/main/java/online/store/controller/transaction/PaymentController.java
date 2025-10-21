package online.store.controller.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import online.store.dto.transaction.RechargeRequest;
import online.store.pojo.Result;
import online.store.service.transaction.PaymentService;
import online.store.vo.transaction.RechargeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "支付", description = "账户充值和商品购买接口")
@RestController
@RequestMapping("/api/payment")
@Validated
@SecurityRequirement(name = "bearerAuth") // 为整个Controller的所有接口应用安全认证需求
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    /**
     * 为当前用户的账户进行充值
     *
     * @param rechargeRequest 包含充值金额的请求体
     * @return 包含充值结果的统一响应体
     */
    @PostMapping("/recharge")
    @PreAuthorize("hasRole('CONSUMER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "账户充值", description = "消费者为自己的账户进行充值。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "充值成功", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "请求参数无效 (如充值金额为负数)", content = @Content(schema =  @Schema(implementation = RechargeResponse.class))),
            @ApiResponse(responseCode = "403", description = "权限不足, 只有消费者可以充值", content = @Content(schema = @Schema(implementation = RechargeResponse.class)))
    })
    public Result<RechargeResponse> recharge(@Valid @RequestBody RechargeRequest rechargeRequest) {
        RechargeResponse response = paymentService.rechargeAccount(rechargeRequest);
        return Result.success(response);
    }

}
