package allein.bizcorn.service.facade;

import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public interface IUserService {
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

    @RequestMapping(value = "/user/byname/{username}",method = RequestMethod.GET)
    @ResponseBody
    public Result<User> getUserByUsername(@PathVariable("username") String userName);

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
    public Result<Integer> updateUser(@RequestParam  User user);
    @PostMapping("/user/bymobile/{mobile}")
    @ResponseBody
    public Result<User> getUserByMobile(@PathVariable("mobile") String mobile);
}
