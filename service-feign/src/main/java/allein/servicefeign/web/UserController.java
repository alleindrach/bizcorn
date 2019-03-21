package allein.servicefeign.web;

import allein.model.output.Result;
import allein.servicefeign.service.ServiceGate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RefreshScope
@RequestMapping("/user")
public class UserController {
    @Autowired
    ServiceGate serviceGate;

    @RequestMapping(value = "/login")
    @ResponseBody
    public Result login(@RequestParam String name, @RequestParam String password) {
        return serviceGate.login(name,password);
    }

}
