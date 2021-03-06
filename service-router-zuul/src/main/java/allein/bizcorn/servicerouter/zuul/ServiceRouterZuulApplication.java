package allein.bizcorn.servicerouter.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableDiscoveryClient
@EnableRedisHttpSession(redisFlushMode = RedisFlushMode.IMMEDIATE)
public class ServiceRouterZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRouterZuulApplication.class, args);
    }

}
