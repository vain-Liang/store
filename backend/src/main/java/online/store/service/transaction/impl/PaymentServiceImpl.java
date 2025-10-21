package online.store.service.transaction.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.store.common.exception.*;
import online.store.common.utils.SnowflakeIdGenerator;
import online.store.dto.transaction.RechargeRequest;
import online.store.enums.TransactionStatus;
import online.store.enums.TransactionType;
import online.store.mapper.ProductMapper;
import online.store.mapper.TransactionMapper;
import online.store.mapper.UserMapper;
import online.store.pojo.Transaction;
import online.store.pojo.User;
import online.store.service.auth.PermissionService;
import online.store.service.transaction.PaymentService;
import online.store.vo.transaction.RechargeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;

//TODO: 日志记录
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserMapper userMapper;
    @Getter
    private final ProductMapper productMapper;
    private final PermissionService permissionService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RechargeResponse rechargeAccount(RechargeRequest rechargeRequest) {
        BigDecimal amount = rechargeRequest.getAmount();

        User user = getCurrentUserForUpdate();

        user.setBalance(user.getBalance().add(amount));

        userMapper.updateById(user);

        // TODO: 日志记录
        long transactionId = snowflakeIdGenerator.nextId();
        // 暂且使用 JDK21 预览功能字符串模板打印到控制台进行模拟日志记录
        System.out.println(STR."充值成功! 流水号 \{transactionId}, 用户 '\{user.getUsername()}' 充值成功。金额: \{amount}, 最新余额: \{user.getBalance()}");

        //TODO: 充值失败设置状态和备注及输出信息
        //TODO: 充值状态为处理中的逻辑判定和处理
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setUserId(user.getId());
        transaction.setOrderId(null); // 充值操作无关联订单ID
        transaction.setAmount(amount);
        transaction.setType(TransactionType.RECHARGE);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionNo(null); // 第三方交易号留空
        transaction.setRemark(String.format("用户[%s]充值[%s]元成功", user.getUsername(), NumberFormat.getCurrencyInstance().format(amount)));

        transactionMapper.insert(transaction);

        return new RechargeResponse(transactionId, amount, user.getBalance(), LocalDateTime.now());
    }

    /**
     * 获取当前登录的用户并施加数据库行级写锁.
     * 这是一个内部辅助方法，用于确保在事务中读取和更新用户数据的一致性.
     *
     * @return 带锁的 User 对象
     * @throws UserNotFoundException 如果无法确定当前用户或用户不存在于数据库中
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
