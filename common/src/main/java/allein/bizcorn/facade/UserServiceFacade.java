package allein.bizcorn.facade;

import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping("/user")
public interface UserServiceFacade {
//    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
//    Result<User> login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);
//    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
//    Result<User> logout();

    @PutMapping("/")
    @ResponseBody
    Result<User> update(
            @RequestParam(value = "mobile") String mobile,
            Long userId,
            HttpSession session,
            HttpServletRequest request
    );
    @GetMapping("/captcha.jpg")
    public void captcha(HttpServletRequest request, HttpServletResponse response) ;

    @GetMapping("/mobile/captcha")
    @ResponseBody
    public Result mobileCaptcha(@RequestParam String mobile) ;
}
