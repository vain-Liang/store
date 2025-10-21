package online.store.common.exception;

/**
 * 用户异常
 */
public class UserNotFoundException extends BaseException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
