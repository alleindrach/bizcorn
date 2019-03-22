package allein.servicefeign.service;

import allein.model.entity.user.User;
import allein.model.output.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = IUserServiceHystric.class
)
public interface IUserService {
    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    Result<User> login(@RequestParam(value = "name") String name, @RequestParam(value = "password") String password);
    @RequestMapping(value = "/user/", method = RequestMethod.PUT)
    Result<User> update(@RequestParam(value = "mobile") String mobile);


}
