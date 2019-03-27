package allein.bizcorn.servicefeign.service;

import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.facade.UserServiceFacade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = UserServiceHystric.class
)
public interface IUserService extends UserServiceFacade{
    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    Result<User> login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    Result<User> logout();
}
