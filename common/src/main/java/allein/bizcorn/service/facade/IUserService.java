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
    Result<IUser> login(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "captcha") String captcha);
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    Result logout(@RequestParam HttpServletRequest request,@RequestParam HttpServletResponse response);

    @RequestMapping(value = "/user",method = RequestMethod.PUT)
    @ResponseBody
    Result<IUser> update(
            @RequestParam(value = "mobile") String mobile,
            @RequestParam HttpSession session,
            @RequestParam HttpServletRequest request
    );

    @RequestMapping(value = "/user/byname/{username}",method = RequestMethod.GET)
    @ResponseBody
    public Result<IUser> getUserByUsername(@PathVariable("username") String userName);

    @RequestMapping(value = "/user/login/errortimes/{username}",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> getUserLoginErrorTimes(@PathVariable("username") String userName);
    @PostMapping("/user/login/errortimes/inc/{username}")
    @ResponseBody
    public Result<Long> incUserLoginErrorTimes(@PathVariable("username")  String userName);
    @PostMapping("/user/login/errortimes/rst/{username}")
    @ResponseBody
    public Result<Boolean> rstUserLoginErrorTimes(@PathVariable("username")String userName);
    @PostMapping("/user/update")
    @ResponseBody
    public Result<Integer> updateUser(@RequestParam  IUser user);
    @PostMapping("/user/bymobile/{mobile}")
    @ResponseBody
    public Result<IUser> getUserByMobile(@PathVariable("mobile") String mobile);
    @PostMapping("/user/authorities/id")
    @ResponseBody
    public Result<List<String>> getUserAuthorities(@PathVariable("id")  String userId);
}
