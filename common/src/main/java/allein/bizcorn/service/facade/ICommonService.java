package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ICommonService {
//    @RequestMapping(value = "/common/captcha.jpg",method = RequestMethod.GET)
//    public void captcha(@RequestParam HttpServletRequest request, @RequestParam HttpServletResponse response) ;

    @RequestMapping(value = "/common/captcha.jpg",method = RequestMethod.GET)
    /*
    @Description:发送图形验证码
    @Param:[]
    @Return:org.springframework.http.ResponseEntity<byte[]>
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:2:47 PM
    */
    public ResponseEntity<byte[]> captcha() ;

    @RequestMapping(value = "/common/mobile/captcha")
    /*
    @Description:发送手机验证码
    @Param:[mobile 手机号, captcha：图形验证码]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:2:33 PM
    */
    public Result mobileCaptcha( @RequestParam String mobile, @RequestParam String captcha) ;


}
