package online.store.common.exception;

/**
 * 商品状态异常
 */
public class ProductNotForSaleException extends BaseException {
    public ProductNotForSaleException(String message) {
        super(message);
    }
}
