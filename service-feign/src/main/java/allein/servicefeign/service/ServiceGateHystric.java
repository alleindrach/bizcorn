package allein.servicefeign.service;

import allein.model.entity.user.User;
import allein.model.exception.CommonException;
import allein.model.output.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class ServiceGateHystric implements ServiceGate {
    @Override
    public Result<User> login(@RequestParam(value = "name") String name, @RequestParam(value = "password") String password)
    {
        return new Result(new CommonException("0000","lost connection to service server"));
    }
}
