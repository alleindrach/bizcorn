package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.service.facade.IUserService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//动态注入proxy
@FeignClient(value = "proxy"
        , fallback = UserServiceHystric.class
)
public interface UserServiceProxy extends IUserService{
    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    Result<User> login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    Result<User> logout();
}
