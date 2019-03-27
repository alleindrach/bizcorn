package allein.bizcorn.facade;

import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public interface UserServiceFacade {
//    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
//    Result<User> login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);
//    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
//    Result<User> logout();

    @RequestMapping(value = "/user",method = RequestMethod.PUT)
    @ResponseBody
    Result<User> update(
            @RequestParam(value = "mobile") String mobile,
            @RequestParam HttpSession session,
            @RequestParam HttpServletRequest request
    );
    @GetMapping("/user/captcha.jpg")
    public void captcha(@RequestParam  HttpServletRequest request,  @RequestParam  HttpServletResponse response) ;

    @GetMapping("/user/mobile/captcha")
    @ResponseBody
    public Result mobileCaptcha(@RequestParam String mobile) ;
}
