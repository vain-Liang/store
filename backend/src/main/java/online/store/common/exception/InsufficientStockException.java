package online.store.common.exception;

/**
 * 商品库存异常
 */
public class InsufficientStockException extends BaseException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
