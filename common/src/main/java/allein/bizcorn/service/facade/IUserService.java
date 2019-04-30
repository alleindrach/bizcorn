package allein.bizcorn.service.facade;


import allein.bizcorn.model.facade.IAuthority;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.output.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;


public interface IUserService {
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    Result login(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "captcha",required = false) String captcha);
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    Result logout();

    @RequestMapping(value = "/user",method = RequestMethod.PUT)
    Result update(
            @RequestParam(value = "mobile") String mobile
    );

    @RequestMapping(value = "/user/byname/{username}",method = RequestMethod.GET)
    public Result<IUser> getUserByUsername(@PathVariable("username") String userName);

    @RequestMapping(value = "/user/masked/{username}",method = RequestMethod.GET)
    public Result<IUser> getMaskedUserByUsername(@PathVariable("username") String userName);


    @RequestMapping(value = "/user/login/errortimes/{username}",method = RequestMethod.GET)
    public Result<Long> getUserLoginErrorTimes(@PathVariable("username") String userName);

    @RequestMapping("/user/login/errortimes/inc/{username}")
    public Result<Long> incUserLoginErrorTimes(@PathVariable("username")  String userName);

    @RequestMapping("/user/login/errortimes/rst/{username}")
    public Result<Boolean> rstUserLoginErrorTimes(@PathVariable("username")String userName);

    @RequestMapping("/user/update")
    public Result<Integer> updateUser(@RequestParam  IUser user);

    @RequestMapping("/user/bymobile/{mobile}")
    public Result<IUser> getUserByMobile(@PathVariable("mobile") String mobile);

    @RequestMapping("/user/authorities/id")
    public Result<List<String>> getUserAuthorities(@PathVariable("id")  String userId);


    @RequestMapping("/user/homepage")
    public Result<IUser> fetchHomepage();

    @RequestMapping(value = "/user/register",method = RequestMethod.PUT)
    Result register(
            @RequestParam HttpServletRequest request,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mobileCaptcha") String captcha,
            @RequestParam(value = "mobile") String mobile
    );
    @RequestMapping(value = "/kid/register/{mac}",method = RequestMethod.PUT)
    Result register(
            @PathVariable(value = "mac") String mac
    );

}
