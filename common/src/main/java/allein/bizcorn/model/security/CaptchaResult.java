package allein.bizcorn.model.security;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 验证码结果封装
 *
 * @author bojiangzhou 2018/08/10
 */
public class CaptchaResult implements Serializable {

    public static final String FIELD_CAPTCHA = "captcha";
    public static final String FIELD_CAPTCHA_KEY = "captchaKey";
    public static final String FIELD_LAST_CHECK_KEY = "lastCheckKey";

    /**
     * 验证码
     */
    @Getter
    @Setter
    private String captcha;
    /**
     * 缓存KEY
     */
    @Getter
    @Setter
    private String captchaKey;
    /**
     * 前置验证KEY
     */
    @Getter
    @Setter
    private String lastCheckKey;
    /**
     * 消息
     */
    @Getter
    @Setter
    private String message;
    /**
     * 是否成功
     */
    @Getter
    @Setter
    private boolean success;

    /**
     * 清除验证码
     */
    public void clearCaptcha() {
        this.captcha = null;
    }

}
