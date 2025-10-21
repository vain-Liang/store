package online.store.common.exception;

/**
 * 自定义运行时异常基类
 */
public class BaseException extends RuntimeException {

    public  BaseException() {
        super();
    }

    public BaseException(String message){
        super(message);
    }
}
