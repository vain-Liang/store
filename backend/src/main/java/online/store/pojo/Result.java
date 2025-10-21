package online.store.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回
 * @param <T> 数据泛型
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 状态码
     * 成功时, 状态码为0
     * 失败时，对应业务错误码
     */
    private Integer code;

    /**
     * 错误信息，提示用户
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功结果返回
     * @param data 数据
     * @return 泛型，根据业务决定
     * @param <T> 业务数据类型
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 失败结果返回
     * @param message 失败信息
     * @return 泛型, 根据业务决定
     * @param <T> 业务数据类型
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMessage(message);
        return  result;
    }
}
