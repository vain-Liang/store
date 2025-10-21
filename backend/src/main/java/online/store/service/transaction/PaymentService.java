package online.store.service.transaction;

import online.store.dto.transaction.RechargeRequest;
import online.store.vo.transaction.RechargeResponse;

/**
 * 支付服务接口
 * <p>定义账户充值及商品购买相关</p>
 * <p>写操作应具备事务性, 处理并发场景</p>
 */
public interface PaymentService {

    /**
     * 为当前登录用户账户进行充值。
     * 整个操作在事务中进行，并使用行锁保证并发安全。
     *
     * @param rechargeRequest 包含充值金额的请求对象
     * @return 包含充值详情和最新余额的响应对象
     * @throws online.store.exception.UsernameNotFoundException 如果当前登录用户不存在抛出异常
     * @throws IllegalArgumentException 充值金额无效 (如非正数)
     */
    RechargeResponse rechargeAccount(RechargeRequest rechargeRequest);

}
