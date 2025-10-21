package online.store.common.exception;

/**
 * 账户余额异常
 */
public class InsufficientBalanceException extends BaseException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
