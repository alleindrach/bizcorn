1 代码结构
    bizcorn根目录的pom.xml是根项目，其设定了依赖项的版本。
    eurekaserver为服务注册中心
        http://127.0.0.1:8761/
    service 一个服务提供者，为eureka的client
        注解@EnableEurekaClient 表明自己是一个eurekaclient
        注意其defaultZone指向了EurekaServer的defaultzone
        其自身的服务地址是
        http://127.0.0.1:8762
    service-ribbon 负载均衡服务，
    service-feign:=ribbon+rest 负载均衡的另一种实现
        @EnableFeignClients
        定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。
        比如下面这个注释，@FeignClient(value="service") ,指明其需要从注册服务器寻求service 服务的一个实例，而@RequestMapping(value="/hi"),指明其调用service服务的hi接口。
        @FeignClient(value = "service",fallback = ServiceGateHystric.class)
        public interface ServiceGate {
            @RequestMapping(value = "/hi",method = RequestMethod.GET)
            String sayHiFromClientOne(@RequestParam(value = "name") String name);
        }
    断路器的实现
        service-ribbon:
        @HystrixCommand(fallbackMethod = "hiError")
        service-feign:
        @FeignClient(value = "service",fallback = ServiceGateHystric.class)
        
    路由器实现
        service-router-zuul
        类似于nginx的配置
        Filter ：可以在此实现登录态的校验。
    配置服务器实现
        @EnableConfigServer
        
        url:{application}/{profile}/{label}
        file:/application-profile.properties
        
        http://localhost:8888/service-dev.properties
        http://localhost:8888/service/dev
        
    调用配置服务器的演示：service 调取配置服务器的配置项
        service 增加依赖
            <dependency>
        			<groupId>org.springframework.cloud</groupId>
        			<artifactId>spring-cloud-starter-config</artifactId>
        	</dependency>
        service 配置 配置服务器选项
            spring:
              application:
                name: service
              cloud:
                config:
                  label: master
                  profile: dev
                  uri: http://localhost:8888/
                  
        对应到服务器上的
            serive-dev.properties 文件
        引用配置项：
            @Value("${foo}")
        	String foo;
        	
    配置服务器config-server向尤里卡服务器注册
       增加依赖项
       		<dependency>
       			<groupId>org.springframework.cloud</groupId>
       			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
       		</dependency>
       增加配置项
           eureka:
             client:
               serviceUrl:
                 defaultZone: http://localhost:8761/eureka/
       增加尤里卡客户端启动项：
           ...
           @EnableEurekaClient
           public class ServiceApplication {
           ...
    Service从尤里卡服务器调用配置服务
            spring:
              application:
                name: service
              cloud:
                config:
                  label: master
                  profile: dev
                  discovery: <--增加项
                    enabled: true 
                    service-id: config-server <-- 这里和config-server的配置项 spring.application.name 一致
    两种访问配置项的方式
        @Value("${foo}") 修饰的字段
        @ConfigurationProperties()  修饰的组件
        
    配置刷新：    
        cloud 2.0 之前：只要加入依赖:
        
        <dependency>
           <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
         </dependency>
        
        并且在类上,变量上打上@RefreshScope的注解,在启动的时候,都会看到
        
        RequestMappingHandlerMapping : Mapped "{/refresh,methods=[post]}" 
        也就是SpringCloud暴露了一个接口 /refresh 来给我们去刷新配置,但是SpringCloud 2.0.0以后,有了改变.
        
        我们需要在bootstrap.yml里面加上需要暴露出来的地址
        management:
          endpoints:
            web:
              exposure:
                include: refresh,health
       
        现在的地址也不是/refresh了,而是/actuator/refresh
         curl -X POST  -H  "Content-Tapplication/json"  "http://127.0.0.1:8762/actuator/refresh"
        https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html
    Cloud Bus:
         docker-compose.yml 
         
         version: '2'
         
         services:
           zookeeper:
             image: wurstmeister/zookeeper
             restart: unless-stopped
             ports:
               - "2181:2181"
             container_name: zookeeper
         
           # kafka version: 1.1.0
           # scala version: 2.12
           kafka:
             image: wurstmeister/kafka
             ports:
               - "9092:9092"
             environment:
               KAFKA_ADVERTISED_HOST_NAME: localhost
               KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
               KAFKA_BROKER_ID: 1
               KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
               KAFKA_CREATE_TOPICS: "stream-in:1:1,stream-out:1:1"
             depends_on:
               - zookeeper
             container_name: kafka
             
         docker-compose build    
         docker-compose up -d    
         docker exec -it kafka /bin/bash
         export ZK=zookeeper:2181
         $KAFKA_HOME/bin/kafka-topics.sh --create --topic topic --partitions 4 --zookeeper $ZK --replication-factor 1
         $KAFKA_HOME/bin/kafka-topics.sh --zookeeper $ZK --list
         
         
         Config的server/client 都要加
         
         		<dependency>
         			<groupId>org.springframework.cloud</groupId>
         			<artifactId>spring-cloud-starter-bus-kafka</artifactId>
         		</dependency>
         		
         server:
                spring:
                    cloud:
                        stream:
                            kafka:
                                binder:
                                    brokers: localhost:9092
                                    zk-nodes: localhost:2181
         client:
         		<dependency>
         			<groupId>org.springframework.cloud</groupId>
         			<artifactId>spring-cloud-config-monitor</artifactId>
         		</dependency>

                spring:
                   cloud:
                        config:
                            label: master
                            profile: dev
                            name: service
                        discovery:
                            enabled: true
                            service-id: config-server
                        stream:
                            kafka:
                                binder:
                                    brokers: localhost:9092
                                    zk-nodes: localhost:2181
                management:
                    endpoints:
                        web:
                            exposure:
                                include: refresh,bus-refresh
                                
                调用任何一个client的 bus-refresh,都会刷新其他client端的配置实例
                     curl -X POST  -H  "Content-Tapplication/json"  "http://127.0.0.1:8762/actuator/bus-refresh"
             
    定时任务：
        application 加标注 @EnableScheduling
        Task类加@component标注，方法加 @Scheduled 标注
        http://www.cnblogs.com/dannyyao/p/7691871.html
        
    Mybitis 数据库连接
        1 用@Mapper/@Select 方法
        2 用@Mapper/xml方式
        3 配置项
    
    缓存
        1 增加对data-redis 的依赖
        2 增加RedisCacheConfig 配置
        3 在App上增加@EnableCaching标注
        4 需要缓存的函数上增加 @Cacheable(value="zone")，这里，zone是缓存分区，生成的key是zone::key
        
    连接池
                
2 调试
    <build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<jvmArguments>
						-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8089
					</jvmArguments>
				</configuration>
			</plugin>
		</plugins>
	</build>
	使用mvn spring-boot:run运行
	或者
	spring-boot:run "-Drun.jvmArguments=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8089" "-Darguments=server.port=8063"
	
3 版本配合
    注意springcloud Finchley.RELEASE 配合 springframework.boot 2.0.3.RELEASE 版本
    注意springcloud Greenwich.RELEASE 配合 springframework.boot 2.1.3.RELEASE 版本
    
    否则会出现抽象函数空的问题。
    
    
        
        
        REF
        https://blog.csdn.net/forezp/article/details/81040925