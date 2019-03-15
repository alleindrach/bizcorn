package allein.servicefeign.web;

import allein.servicefeign.service.ServiceGate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    ServiceGate serviceGate;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        return serviceGate.sayHiFromClientOne( name)+"@Feign" ;
    }

}
