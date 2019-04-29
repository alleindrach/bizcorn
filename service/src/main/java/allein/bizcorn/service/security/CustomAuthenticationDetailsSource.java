package allein.bizcorn.service.security;


import allein.bizcorn.model.security.CustomWebAuthenticationDetails;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import allein.bizcorn.service.security.config.SecurityConstants;

/**
 * 自定义获取AuthenticationDetails 用于封装传进来的验证码
 *
 * @author bojiangzhou 2018/09/18
 */
@Component
public class CustomAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Autowired
    private CaptchaImageHelper captchaImageHelper;

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
        String cacheCaptcha = captchaImageHelper.getCaptcha(request, SecurityConstants.SECURITY_KEY);
        request.setAttribute(CustomWebAuthenticationDetails.FIELD_CACHE_CAPTCHA, cacheCaptcha);
        return new CustomWebAuthenticationDetails(request);
    }

}