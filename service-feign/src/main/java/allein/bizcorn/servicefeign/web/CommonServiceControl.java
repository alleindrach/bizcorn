package allein.bizcorn.servicefeign.web;


import allein.bizcorn.model.output.Result;
import allein.bizcorn.servicefeign.proxy.CommonServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<byte[]> captcha()
    {
        ResponseEntity<byte[]> result=commonService.captcha();
        return result;
    }
    @GetMapping("/mobile/captcha")
    @ResponseBody
    public Result mobileCaptcha(@RequestParam String mobile,@RequestParam String captcha)
    {
        return commonService.mobileCaptcha(null, null,mobile,captcha);
    }
}
