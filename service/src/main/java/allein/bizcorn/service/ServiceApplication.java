package allein.bizcorn.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
@RefreshScope
@EnableScheduling
@EnableCaching
@EnableRedisHttpSession(
        redisFlushMode = RedisFlushMode.IMMEDIATE,
        maxInactiveIntervalInSeconds = 86400*30 /*过期时间=30天*/)
@ComponentScan(basePackages = "allein.bizcorn" )
@EnableTransactionManagement
public class ServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceApplication.class);
//
//	@Autowired
//	Configuration config;
//
//	@Autowired
//	private KafkaTemplate<Object, Object> kafkaTemplate;
//

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
//
//	@Value("${server.port}")
//	String port;
//
//	@Value("${foo}")
//	String foo;
//
//
//	@Value("${spring.kafka.template.default-topic}")
//	private String topic;
//
//	@RequestMapping("/hi")
//	public String home(@RequestParam(value = "name", defaultValue = "forezp") String name) {
//		return "hi " + name+",conf:"+foo+", instconfi:"+config.getFoo() + " ,i am from port:" + port;
//	}
//	@RequestMapping("/sendmsg")
//	public void send(String message){
//		LOG.info("sending message='{}' to topic='{}'", message, topic);
//		kafkaTemplate.send(topic, message);
//	}
//
//	@KafkaListener(topics = "${spring.kafka.template.default-topic}")
//	public void receive(@Payload String message,
//						@Headers MessageHeaders headers) {
//		LOG.info("received message='{}'", message);
//		headers.keySet().forEach(key -> LOG.info("{}: {}", key, headers.get(key)));
//	}
}



