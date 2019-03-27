package allein.bizcorn.servicefeign.web;


import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.servicefeign.proxy.CommonServiceProxy;
import allein.bizcorn.servicefeign.proxy.UserServiceProxy;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@RestController
@RefreshScope
@RequestMapping("/common")
public class CommonServiceControl {

    private static final Logger logger = LoggerFactory.getLogger(CommonServiceControl.class);


    @Autowired
    CommonServiceProxy commonService;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @GetMapping("/captcha.jpg")
    public void captcha(HttpServletRequest request,HttpServletResponse response)
    {
        commonService.captcha(request,response);
    }
    @GetMapping("/captcha2.jpg")
    public ResponseEntity<byte[]> captcha2(HttpServletRequest request, HttpServletResponse response)
    {
        ResponseEntity<byte[]> result=commonService.captcha2();
        return result;
    }
    @GetMapping("/mobile/captcha")
    @ResponseBody
    public Result mobileCaptcha(@RequestParam String mobile)
    {
        return commonService.mobileCaptcha(mobile);
    }
}
