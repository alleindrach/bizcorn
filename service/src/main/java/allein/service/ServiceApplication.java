package allein.service;

import allein.service.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ServiceApplication {
	@Autowired
	Configuration config;

	public static void main(String[] args) {
		SpringApplication.run( ServiceApplication.class, args );
	}

	@Value("${server.port}")
	String port;

	@Value("${foo}")
	String foo;

	@RequestMapping("/hi")
	public String home(@RequestParam(value = "name", defaultValue = "forezp") String name) {
		return "hi " + name+",conf:"+foo+", instconfi:"+config.getFoo() + " ,i am from port:" + port;
	}

}



