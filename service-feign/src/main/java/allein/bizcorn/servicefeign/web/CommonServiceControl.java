package allein.bizcorn.servicefeign.web;


import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.model.security.CaptchaResult;
import allein.bizcorn.service.facade.gate.ICommonServiceGate;
import allein.bizcorn.servicefeign.proxy.CommonServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RefreshScope
public class CommonServiceControl implements ICommonServiceGate{

    private static final Logger logger = LoggerFactory.getLogger(CommonServiceControl.class);


    @Autowired
    CommonServiceProxy commonService;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;


    public ResponseEntity<byte[]> captcha()
    {
        ResponseEntity<byte[]> result=commonService.captcha();
        return result;
    }
   
    public Result mobileCaptcha(@RequestParam String mobile,@RequestParam String captcha)
    {
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        Result result= commonService.mobileCaptcha(mobile,captcha);
        if(result.isSuccess())
        {
            Map captchaResult= (Map) result.getData();
            Cookie captchaKeyCookie=new Cookie(SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME,(String)captchaResult.get("captchaKey"));
            captchaKeyCookie.setPath("/");
            response.addCookie(captchaKeyCookie);
        }
        return result;
    }
}
