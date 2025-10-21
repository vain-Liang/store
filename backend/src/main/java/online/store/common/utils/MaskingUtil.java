package online.store.common.utils;

/**
 * 数据脱敏工具类
 */
public class MaskingUtil {

    /**
     * 邮箱脱敏
     * @param email 登录用户邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String prefix = parts[0];
        if (prefix.length() <= 2) {
            prefix = prefix.charAt(0) + "*";
        } else {
            prefix = prefix.substring(0, 2) + "****";
        }
        return prefix + "@" + parts[1];
    }

    /**
     * 电话号脱敏
     * @param phone 登录用户电话号
     * @return 脱敏后的电话号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
