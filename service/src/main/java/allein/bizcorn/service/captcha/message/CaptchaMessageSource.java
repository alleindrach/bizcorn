package allein.bizcorn.service.captcha.message;


import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class CaptchaMessageSource {

    private CaptchaMessageSource() {}

    private static final MessageSource messageSource;

    static {
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasename("allein.bizcorn.captcha.messages");
        bundleMessageSource.setDefaultEncoding("UTF-8");
        bundleMessageSource.setUseCodeAsDefaultMessage(true);
        messageSource = bundleMessageSource;
    }

    /**
     * @param code code
     * @return message
     */
    public static String getMessage(String code) {
        return messageSource.getMessage(code, null, code, Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * @param code code
     * @param args args
     * @return message
     */
    public static String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, code,Locale.SIMPLIFIED_CHINESE);
    }
}
