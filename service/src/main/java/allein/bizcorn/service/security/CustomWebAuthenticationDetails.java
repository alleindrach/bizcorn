package allein.bizcorn.service.security;

import allein.bizcorn.service.captcha.CaptchaResult;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import javax.servlet.http.HttpServletRequest;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    public static final String FIELD_CACHE_CAPTCHA = "cacheCaptcha";

    private String inputCaptcha;
    private String cacheCaptcha;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        cacheCaptcha = (String) request.getAttribute(FIELD_CACHE_CAPTCHA);
        inputCaptcha = request.getParameter(CaptchaResult.FIELD_CAPTCHA);
    }

    public String getInputCaptcha() {
        return inputCaptcha;
    }

    public String getCacheCaptcha() {
        return cacheCaptcha;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        CustomWebAuthenticationDetails that = (CustomWebAuthenticationDetails) object;

        return inputCaptcha != null ? inputCaptcha.equals(that.inputCaptcha) : that.inputCaptcha == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (inputCaptcha != null ? inputCaptcha.hashCode() : 0);
        return result;
    }
}