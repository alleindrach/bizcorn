package allein.serviceribbon.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceGate {
    @Autowired
    RestTemplate restTemplate;


    @HystrixCommand(fallbackMethod = "hiError")
//    @RefreshScope
    public String hiService(String name) {
        String result = restTemplate.getForObject("http://proxy/hi?name=" + name, String.class);
        return result;
    }

    public String hiError(String name) {
        return "hi," + name + ",sorry,error!";
    }

}
