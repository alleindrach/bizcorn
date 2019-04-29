package allein.bizcorn.model.security;

/**
 * 验证码结果封装
 *
 * @author bojiangzhou 2018/08/10
 */
public class CaptchaResult {

    public static final String FIELD_CAPTCHA = "captcha";
    public static final String FIELD_CAPTCHA_KEY = "captchaKey";
    public static final String FIELD_LAST_CHECK_KEY = "lastCheckKey";

    /**
     * 验证码
     */
    private String captcha;
    /**
     * 缓存KEY
     */
    private String captchaKey;
    /**
     * 前置验证KEY
     */
    private String lastCheckKey;
    /**
     * 消息
     */
    private String message;
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 清除验证码
     */
    public void clearCaptcha() {
        this.captcha = null;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getLastCheckKey() {
        return lastCheckKey;
    }

    public void setLastCheckKey(String lastCheckKey) {
        this.lastCheckKey = lastCheckKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
