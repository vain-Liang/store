package online.store.common.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import online.store.pojo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Hidden // 解除 @RestControllerAdvice 注解造成的冲突, 详见 https://stackoverflow.com/questions/79274106/how-to-use-both-restcontrolleradvice-and-swagger-ui-in-spring-boot
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有自定义的业务逻辑异常 (继承自 BusinessException)。
     * <p>
     * 余额不足、库存不足、商品未上架等。
     * 这些是可预期的客户端错误，返回 HTTP 400。
     * </p>
     *
     * @param e 业务异常
     * @return 包含具体错误信息的
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BaseException e) {
        // TODO: 日志记录
        // log.warn("业务逻辑校验失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(e.getMessage()));
    }

    /**
     * 处理非法参数异常, 业务逻辑异常 (例如: 用户名已存在, 密码错误等)
     * @param e IllegalArgumentException 捕获的异常
     * @return 400 Bad Request, 包含错误信息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e){
        return Result.error("请求错误：" + e.getMessage());
    }

    /**
     * 处理请求参数校验失败异常
     * @param e MethodArgumentNotValidException 捕获的异常
     * @return 400 Bad Request, 包含字段错误信息的 Map
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Result<Map<String, String>> result = new Result<>();
        result.setCode(1);
        result.setMessage("请求参数校验失败");
        result.setData(errors);
        return result;
    }

    /**
     * 处理请求参数缺失异常
     * @param e MissingServletRequestParameterException
     * @return 400 Bad Request
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return Result.error("请求参数缺失: " + e.getParameterName());
    }

    /**
     * 处理请求体格式错误异常 (例如, JSON格式错误)
     * @param e HttpMessageNotReadableException
     * @return 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return Result.error("请求体格式错误或服务器无法识别");
    }

    /**
     * 处理认证失败异常, JWT Access 或 Refresh Token无效或过期
     * @param e AuthenticationException 捕获的异常
     * @return 401 Unauthorized, 包含错误信息
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleAuthenticationException(AuthenticationException e) {
        return Result.error("认证失败: " + e.getMessage());
    }

    /**
     * 请求方法错误异常
     * @param e HttpRequestMethodNotSupportedException 捕获异常
     * @return 405 Method Not Allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public  Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        assert e.getSupportedMethods() != null;
        return Result.error("请求方法 '"+ e.getMethod() + "' 错误, 请使用 " + String.join(",", e.getSupportedMethods()));
    }

    /**
     * 处理不支持的媒体类型异常
     * @param e HttpMediaTypeNotSupportedException
     * @return 415 Unsupported Media Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return Result.error("不支持的媒体类型: " + e.getContentType());
    }

    /**
     * 权限不足异常
     * @param e SpringSecurity AccessDeniedException 异常
     * @return 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<?> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error("账户权限不足");
    }

    /**
     * 处理404 Not Found异常 (特别是对于API路径)
     * 注意：这可能需要额外配置才能捕获所有404
     * @param e NoResourceFoundException
     * @return 404 Not Found
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoResourceFoundException(NoResourceFoundException e) {
        return Result.error("请求的资源不存在: " + e.getResourcePath());
    }

    /**
     * 处理其他 JSR-303 校验异常，如 @RequestParam, @PathVariable
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        // TODO: 日志记录
        // log.warn("请求参数校验失败: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 通用异常处理, 兜底, 作为最后防线
     * 处理所有未捕获的异常, 避免暴露内部错误信息
     * @param e Exception 捕获的异常
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e){
        // TODO: 日志记录
        // log.error("服务器错误", e);
        e.printStackTrace(); // 开发阶段打印堆栈信息
        return Result.error("服务器内部错误,请稍后再试");
    }
}
