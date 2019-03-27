package allein.bizcorn.service.facade;

import allein.bizcorn.common.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ICommonService {
    @RequestMapping(value = "/common/captcha.jpg",method = RequestMethod.GET)
    public void captcha(@RequestParam HttpServletRequest request, @RequestParam HttpServletResponse response) ;

    @RequestMapping(value = "/common/captcha2.jpg",method = RequestMethod.GET)
    public ResponseEntity<byte[]> captcha2() ;

    @RequestMapping(value = "/common/mobile/captcha",method = RequestMethod.GET)
    @ResponseBody
    public Result mobileCaptcha(@RequestParam String mobile) ;

}
