package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.entity.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.model.security.CaptchaResult;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.db.mysql.dao.UserDAO;
import allein.bizcorn.service.facade.ICommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RefreshScope
public class CommonServiceImpl implements ICommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Value("${bizcorn.session.prefix}")
    String sessionPrefix;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @Value("${bizcorn.session.attribute.timeout}")
    String sessionAttrTimeout;

    @Value("${bizcorn.session.timeout}")
    Long sessionTimeout;

    @Autowired
    ICacheAccessor cacheAccessor;

    @Autowired
    UserDAO userDAO;

    @Autowired
    private CaptchaImageHelper captchaImageHelper;

    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;

//    public void captcha(HttpServletRequest request, HttpServletResponse response) {
//        captchaImageHelper.generateAndWriteCaptchaImage(request,response, SecurityConstants.SECURITY_KEY);
//    }
    public ResponseEntity<byte[]> captcha() {
        return captchaImageHelper.captchaImage(SecurityConstants.SECURITY_KEY);
    }

    public Result mobileCaptcha(@RequestParam String mobile, @RequestParam String captcha) {
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();

        String cacheCaptcha = captchaImageHelper.getCaptcha(request, SecurityConstants.SECURITY_KEY);
        if(cacheCaptcha==null || cacheCaptcha.isEmpty()||cacheCaptcha.compareToIgnoreCase(captcha)!=0)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.CAPTCH_INVALID));
        }
//
//        User params = new User();
//        params.setMobile(mobile);
//        if (userDAO.selectByMobile(mobile) == null) {
//            return  Result.failWithMessage("用户不存在或者未登录");
//        }
//        else
//        {
            CaptchaResult captchaResult = captchaMessageHelper.generateMobileCaptcha(mobile, SecurityConstants.SECURITY_KEY);
            if (captchaResult.isSuccess()) {
                // 模拟发送验证码
                logger.info("【BizCorn】 您的短信验证码是 {}。若非本人发送，请忽略此短信。", captchaResult.getCaptcha());
                captchaResult.clearCaptcha();
                Cookie captchaKeyCookie=new Cookie(SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME,captchaResult.getCaptchaKey());
                captchaKeyCookie.setPath("/");
                response.addCookie(captchaKeyCookie);
                return Result.successWithData(captchaResult);
            }
            return Result.failWithMessage(captchaResult.getMessage());
//        }

    }

}
