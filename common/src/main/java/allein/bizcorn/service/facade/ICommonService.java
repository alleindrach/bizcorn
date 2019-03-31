package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public interface ICommonService {
//    @RequestMapping(value = "/common/captcha.jpg",method = RequestMethod.GET)
//    public void captcha(@RequestParam HttpServletRequest request, @RequestParam HttpServletResponse response) ;

    @RequestMapping(value = "/common/captcha.jpg",method = RequestMethod.GET)
    public ResponseEntity<byte[]> captcha() ;

    @RequestMapping(value = "/common/mobile/captcha",method = RequestMethod.GET)
    @ResponseBody
    public Result mobileCaptcha(@RequestParam String mobile) ;

}
