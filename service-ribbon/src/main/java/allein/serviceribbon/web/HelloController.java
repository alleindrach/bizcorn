package allein.serviceribbon.web;

import allein.serviceribbon.service.ServiceGate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@EnableAutoConfiguration
public class HelloController {

    @Autowired
    ServiceGate serviceGate;

    @Value("${foo}")
    String foo;

    @GetMapping(value = "/hi")
    public String hi(@RequestParam String name) {

        return serviceGate.hiService(name) + "@Ribbon," + foo;
    }

}
