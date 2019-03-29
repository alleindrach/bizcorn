## 目标
  **基于 spring cloud 的框架，实验用。**
  
## 代码结构
 * bizcorn
   - 根项目
   - pom.xml 其设定了依赖项的版本。
 * common  通用库，包含：
     - `annotation`
     
           标注定义，比如方法缓存用标注 CacheMethod30S   
     - `cache`
     
           缓存访问接口和配置，这里假设各个服务都能直接访问缓存，使用redis实现。
     
     - `exception`
     
           异常相关的定义
           
          - `ErrorCodeException-->CommonException`
          - `ExceptionEnum` 异常代码定义
        
     - `model`
           
          - `entity` 数据库实体相关定义
          - `output` 调用接口类型
     - `util`通用工具
        - `keygenerator` 缓存的key生成器
        - `SecurityUtil` 从当前的上下文环境获取用户凭据
     - `service.facade`    服务接口，划分如下
       - `ICacheService` 缓存服务接口
       - `ICommonService` 通用服务接口
       - `IConfigService` 配置服务接口
       - `IMessageService` 消息服务接口
    
 * eurekaserver为服务注册中心
 
        http://127.0.0.1:8761/
               
 * service 
 
        一个服务提供者，为eureka的client
 
        注解@EnableEurekaClient 表明自己是一个eurekaclient
        注意其defaultZone指向了EurekaServer的defaultzone
        其自身的服务地址是
        http://127.0.0.1:8762
    
 * service-feign:=ribbon+rest 负载均衡的一种实现
 
        @EnableFeignClients
        定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务
        
        比如下面这个注释，@FeignClient(value="service") ,指明其需要从注册服务器寻求service 服务的一个实例，而@RequestMapping(value="/hi"),指明其调用service服务的hi接口。
   ```
        @FeignClient(value = "service",fallback = ServiceGateHystric.class)
                public interface ServiceGate {
                    @RequestMapping(value = "/hi",method = RequestMethod.GET)
                String sayHiFromClientOne(@RequestParam(value = "name") String name);
        }
   ```
     
   
        断路器的实现
        
        service-feign:
        @FeignClient(value = "service",fallback = ServiceGateHystric.class)
        
 * service-router-zuul 路由器实现
 
        类似于nginx的配置
        filter ：可以在此实现登录态的校验。
        `WebSocketFilter` 进行http->websocket的协议转换支持，
        
 * config-server 配置服务器实现
        
        @EnableConfigServer
       
         映射关系： 
         url:{application}/{profile}/{label}
         file:/application-profile.properties
         http://localhost:8888/service-dev.properties
         http://localhost:8888/service/dev
         
         application 由客户端的spring.application.name定义，
         profile由客户端 spring.cloud.config.profile定义
         label 由客户端的spring.cloud.config.label定义
         
         
   ```
             spring:
                application:
                    name: config-server
                cloud:
                    config:
                        server:
                            git:
                                uri: file://Users/allein/work/springcloud/bizcorn/config.repo 本地的git库
                        label: master
   ```


## StepByStep
### Step 1st 调用配置服务器的演示：service 调取配置服务器的配置项
#### service 
* 增加依赖:
```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
```
* 配置服务器选项
```        
    spring:
      application:
        name: service
      cloud:
        config:
          label: master
          profile: dev
          uri: http://localhost:8888/ --配置服务器地址
```                  
        上面的配置，对应到git的配置：
            serive-dev.properties 文件
            
* 从尤里卡服务器调用配置服务
```
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
```                
* 引用配置项方式1：
``` 
        @Value("${foo}")
        String foo;
```
* 引用配置项方式2：  

 	    `@ConfigurationProperties()`  修饰的组件
#### 配置服务器config-server
* 向尤里卡服务器注册

      增加依赖项 
```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    
```
      增加配置项
```   
       eureka:
         client:
           serviceUrl:
             defaultZone: http://localhost:8761/eureka/
```
       增加尤里卡客户端启动项：
```        
           ...
           @EnableEurekaClient
           public class ServiceApplication {
           ...
```           
 
   两种访问配置项的方式
        @Value("${foo}") 修饰的字段
        @ConfigurationProperties()  修饰的组件
        
#### 配置刷新    
* cloud 2.0 之前

      只要加入依赖:
```        
        <dependency>
           <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
         </dependency>
```        
        并且在类上,变量上打上@RefreshScope的注解,在启动的时候,都会看到日志：
        
``` 
         RequestMappingHandlerMapping : Mapped "{/refresh,methods=[post]}"
```  
        也就是SpringCloud暴露了一个接口 /refresh 来给我们去刷新配置,但是SpringCloud 2.0.0以后,有了改变.
* cloud 2.0 之后
        
        我们需要在configserver的bootstrap.yml里面加上需要暴露出来的地址
```     
        management:
          endpoints:
            web:
              exposure:
                include: refresh,health
```        
        
        现在的地址也不是/refresh了,而是/actuator/refresh
```        
         curl -X POST  -H  "Content-Tapplication/json"  "http://127.0.0.1:8762/actuator/refresh"
```         

         [参考文档](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)
        
        这里存在的问题是对每个需要进行刷新的服务，都调吊用一次刷新地址。
    
#### 使用Cloud Bus进行配置统一刷新:
* 使用docker 安装配置kafka/redis等
        
        [参考文档] https://www.kaaproject.org/kafka-docker/ kafka的docker方式配置
        
   * 首先创建docker 的网络环境
         
         docker network create --driver bridge --subnet 172.23.0.0/25 --gateway 172.23.0.1  zookeeper_network
            
   * 编写docker-compose.yml
```        
        version: '3.4'
        
        services:
          zoo1:
            image: zookeeper
            restart: always
            hostname: zoo1
            container_name: zoo1
            ports:
            - 2181:2181
            environment:
              ZOO_MY_ID: 1
              ZOO_SERVERS: server.1=0.0.0.0:2888:3888 
            networks:
              default:
                ipv4_address: 172.23.0.11
        
          kafka1:
            image: wurstmeister/kafka
            restart: always
            hostname: kafka1
            container_name: kafka1
            ports:
            - "9092:9092"
            expose: 
            - "9093"
            environment:
              KAFKA_ADVERTISED_LISTENERS: INSIDE://kafka1:9093,OUTSIDE://192.168.2.233:9092
              KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT
              KAFKA_LISTENERS: INSIDE://0.0.0.0:9093,OUTSIDE://0.0.0.0:9092
              KAFKA_ZOOKEEPER_CONNECT: zoo1:2181
              KAFKA_INTER_BROKER_LISTENER_NAME: INSIDE
              KAFKA_BROKER_ID: 1
              JMX_PORT: 9999 
            networks:
              default:
                ipv4_address: 172.23.0.14
          redis:
            image: redis
            container_name: redis
            ports: 
              - "6379:6379"
        
        networks:
          default:
            external:
              name: zookeeper_network
              
              
         docker-compose build    
         docker-compose up -d    
         docker exec -it kafka /bin/bash
         export ZK=zookeeper:2181
         $KAFKA_HOME/bin/kafka-topics.sh --create --topic topic --partitions 4 --zookeeper $ZK --replication-factor 1
         $KAFKA_HOME/bin/kafka-topics.sh --zookeeper $ZK --list
```         
         注意，这里我们假设宿主机的ip地址为192.168.2.233,kakfa的关键配置是其公告地址的配置
         
         
         
   * Config的server/client 都要加
   
     * 依赖项
```         
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-kafka</artifactId>
        </dependency>
```
    
    
   * * server加配置项
     
     
```
        spring:
            cloud:
                stream:
                    kafka:
                        binder:
                            brokers: 192.168.2.233:9092
                            zk-nodes: 192.168.2.233:2181
```
   * * client加配置项         
```
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
                            brokers: 192.168.2.233:9092
                            zk-nodes: 192.168.2.233:2181
        management:
            endpoints:
                web:
                    exposure:
                        include: refresh,bus-refresh
```                                
* * 测试               
         
        调用任何一个client的 bus-refresh,都会刷新其他client端的配置实例
    ```      
         curl -X POST  -H  "Content-Tapplication/json"  "http://127.0.0.1:8762/actuator/bus-refresh"
    ```

### Step 2nd 定时任务

* application 加标注 
    ```
    @EnableScheduling
    ```
* Task类加`@component`标注以便生成`bean`注入
* 定时方法加 `@Scheduled` 标注

      [参考文档]http://www.cnblogs.com/dannyyao/p/7691871.html
  
      [遗留问题]定时服务的线程和并发问题。
    
    
### Step 3rd 消息发送、接收
>使用kafka实现

        注入kafka 模版 
```        
        	@Autowired
        	private KafkaTemplate<Object, Object> kafkaTemplate;
```
        增加发送器和监听器
```         
        	@RequestMapping("/sendmsg")
        	public void send(String message){
        		LOG.info("sending message='{}' to topic='{}'", message, topic);
        		kafkaTemplate.send(topic, message);
        	}

        	@KafkaListener(topics = "${spring.kafka.template.default-topic}")
        	public void receive(@Payload String message,
        						@Headers MessageHeaders headers) {
        		LOG.info("received message='{}'", message);
        		headers.keySet().forEach(key -> LOG.info("{}: {}", key, headers.get(key)));
        	}
``` 
### Step 4th 使用Mybitis连接Mysql
* 增加依赖
```
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
```        
                        
* 方式一：用@Mapper/xml方式
```
    mybatis:
        mapperLocations: classpath:mappers/*.xml
        configuration:
        # 使全局的映射器启用或禁用缓存
            cacheEnabled: true
        # 使用驼峰命名法转换字段。
            mapUnderscoreToCamelCase: true

```
* 方式二：用@Mapper/@Select 方法

```
    @org.apache.ibatis.annotations.Update(
            {"<script>",
                    "update users" ,
                            "<set >" ,
                            " <if test='username != null' > " ,
                            "   username = #{username,jdbcType=VARCHAR}," ,
                            " </if>" ,
                            " <if test='password != null' >" ,
                            "    password = #{password,jdbcType=VARCHAR}," ,
                            " </if>" ,
                            " <if test= 'enabled != null' > " ,
                            "    enabled = #{enabled,jdbcType=INTEGER} " ,
                            " </if>" ,
                            " <if test= 'mobile != null' > " ,
                            "    mobile = #{mobile,jdbcType=VARCHAR} " ,
                            " </if>" ,
                            "</set>" ,
                            "where id = #{id,jdbcType=INTEGER}",
                    "</script>"})
    int update(User user);        
        
```        
     注意此处由于有mybatis的脚本，所以前面加上<script>标签。
     
* 配置项

```
   spring: 
        datasource:
            url: jdbc:mysql://192.168.2.177:3306/bizcorn
            username: root
            password: 123456
            driver-class-name: com.mysql.jdbc.Driver
            max-idle: 10
            max-wait: 10000
            min-idle: 5
            initial-size: 5
    #自动提交
            default-auto-commit: true
    #指定updates是否自动提交
            auto-commit: true
            validation-query: SELECT 1
            test-on-borrow: false
            test-while-idle: true
    # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            time-between-eviction-runs-millis: 18800
    # 配置一个连接在池中最小生存的时间，单位是毫秒
            minEvictableIdleTimeMillis: 300000
```

* 数据库连接池
           
      默认使用hikariCP ，增加依赖starter-jdbc
      
* 数据库失效处理
      
            
* 数据库对象生成代码对象 maven: plugin

        mybatisplus-maven-plugin
        
### Step 5th 事务：
   
     @EnableTransactionManagement 
     @Transactional
        
        
### Step 6th 缓存
* 增加对data-redis 的依赖
    
      加在common模块里，其他模块从此导入
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
        </dependency>
```        
* 增加redis配置项
```
    redis:
        host: 192.168.2.233
        port: 6379
        #password=bigdata123
        #database=15
        timeout: 5000
        pool:
            maxTotal: 8
            maxWaitMillis: 1000
```            
* 增加RedisCacheConfig 配置
```
    @Bean
        public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
            //初始化一个RedisCacheWriter
            RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
            //设置CacheManager的值序列化方式为json序列化
            RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
            RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
                    .fromSerializer(jsonSerializer);
            RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeValuesWith(pair);
            //设置默认超过期时间是30秒
            defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
            //初始化RedisCacheManager
            return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
        }
```
      注意redisTemplate 的序列化插件，使用string序列化，而不是java序列化，否则会出现key的一些前缀问题。
        

* 在App上增加@EnableCaching标注
```
        @SpringBootApplication
        ...
        
        @EnableCaching
        ...
        public class ServiceApplication {
        ...
```

* 需要缓存的函数上增加  @Cacheable
```
        @Cacheable(value="zone")，
        #这里，zone是缓存分区，生成的key是
        zone::key
            
```

* redis 访问方法：

        redis-cli KEY *
        
* 缓存的超时时间问题？
      

        
### Step 7th Session管理
*增加依赖
```
    <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
    </dependency>
```                
*app增加注释
            
            `@EnableRedisHttpSession`
            
            
*redis-cli 访问：
```
        key *    
        type  "spring:session:sessions:4a176cbc-6f9a-437b-9cc0-fe6e54746d43"    
        hgetall  "spring:session:sessions:4a176cbc-6f9a-437b-9cc0-fe6e54746d43"
```        
        
*session 穿透

        Client  ---1--->  Router ---2--->  Feign ---3--->  Service
        2: sensitiveHeaders: "*"
        3: 如果使用熔断器，熔断器的隔离模式：
           hystrix:
             command:
               default:
                 execution:
                   isolation:
                     strategy: SEMAPHORE
           注入一个SessionInterceptor bean 转发cookie
           
           或者使用下面文章中提到的方式：
           https://blog.csdn.net/zl1zl2zl3/article/details/79084368
### Step 8th 使用Security进行身份认证
* 数据模型
                
      [参考文档]https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/
```        
        drop table if exists  users cascade ;
        create table users(
            id int(16) auto_increment  primary key,
            username varchar(50) not null unique ,
            password varchar(50) not null,
            mobile varchar(20) not null unique ,
            enabled boolean not null
        );
        
        drop table if exists  authorities cascade ;
        create table authorities (
            id int(16) auto_increment  primary key,
            username varchar(50) not null,
            authority varchar(50) not null,
            constraint fk_authorities_users foreign key(username) references users(username)
        );
        
        create unique index ix_auth_username on authorities (username,authority);
        
        
        # Spring Security 2.0 introduced support for group authorities in JdbcDaoImpl. The table structure if groups are enabled is as follows. You will need to adjust this schema to match the database dialect you are using.
        
        
        drop table if exists  groups cascade ;
        create table groups (
            id int(16) auto_increment  primary key,
            group_name varchar(50) not null
        );
        create unique index ix_group_name on groups (group_name);
        
        drop table if exists  group_authorities cascade ;
        create table group_authorities (
            id int(16) auto_increment  primary key,
            group_id bigint not null,
            authority varchar(50) not null,
            constraint fk_group_authorities_group foreign key(group_id) references groups(id)
        );
        
        drop table if exists  group_members cascade ;
        create table group_members (
            id int(16) auto_increment  primary key,
            username varchar(50) not null,
            group_id bigint not null,
            constraint fk_group_members_group foreign key(group_id) references groups(id)
        );
        
        # Persistent Login (Remember-Me) Schema
        drop table if exists  persistent_logins cascade ;
        create table persistent_logins (
            id int(16) auto_increment  primary key,
            username varchar(64) not null,
            series varchar(64)  not null unique ,
            token varchar(64) not null,
            last_used timestamp not null
        );
```        
        Security:
            WebSecurityConfig:注意关闭crsf，否则需要前后端都要配置crsf的token
            CustomUserDetailsService：负责从数据库里提取需要进行比对的user
            Login：从数据库中取得user的权限，加到session中。
        http://www.cnblogs.com/shihuc/p/5051771.html
        
     [参考文档] https://www.cnblogs.com/chiangchou/p/springboot-2.html

* 增加capthca图形验证码

  + 调用 `/user/captcha.jpg` ，生成captcha，key当做cookie穿到前端，后端的redis中保存key：captcha
        
  + 调用 `/user/login` POST方法，参数username/passwrod/captcha/cookie[key] 时,使用fitler 进行用户验证
 ```
           UsernamePasswordAuthenticationFilter.doFilter 
           -->attemptAuthentication
             -->setDetails
               -->this.authenticationDetailsSource.buildDetails(request)
                                   |
                  CustomAuthenticationDetailsSource[WebSecurityConfig.configure]
                    -->buildDetails  从cookie中取key，从redis中取cachedCaptcha，并保存到request.attributes中,生成一个CustomWebAuthenticationDetails ，中间包含了用户的captcha和缓存中的captcha
               -->this.getAuthenticationManager().authenticate( UsernamePasswordAuthenticationToken authRequest)  此时authRequest.principal=输入的用户名，credentials=输入的密码，details.inputCaptcha=输入的验证码，details.cacheCapthca=缓存的验证码
                  -->parent.authenticate(authRequest)
                        |                    
                     CustomAuthenticationProvider.retrieveUser()=>userDetails 从数据库取出用户信息，包含角色
                                               -->additionalAuthenticationChecks(userDetails,authRequest)   ,1 查 authRequest.details 的验证码 2 查userDetails.password和authRequest.credentials是否一致
                     完成后，返回从数据库取出的用户信息、权限角色，
                     -->CustomAuthenticationSuccessHandler
                        302跳转到loginsuccessurl,此处可以加一下跳转到前一页的处理
```                        
* + 调用 `/user/login` GET方法 显示登录页面                          
                                                                             
                                                 
### Step 9th Eueka对服务失效的感知优化：
        [参考文档] https://yq.aliyun.com/articles/693725
```        
    eureka:
      instance:
        hostname: localhost
      server:  #配置属性，但由于 Eureka 自我保护模式以及心跳周期长的原因，经常会遇到 Eureka Server 不剔除已关停的节点的问题
        enable-self-preservation: false # 设为false，关闭自我保护
        eviction-interval-timer-in-ms: 5000 # 清理间隔（单
```
### Step 10th 代码复用：

       一些可复用的config的类，可以包装成一个jar项目common，以依赖方式加入到其他项目中，比如redis、security配置类     
       服务接口的复用，参见common模块的service.facade包
       
### Step 11th i18n 本地化

### Step 12th Feign的穿透

*    Feign 文件上传
        https://blog.csdn.net/ytzzh0726/article/details/79467843
*    Feign 穿透，可以使用ResponseEntity
        https://blog.csdn.net/Wetsion/article/details/80012823
        
    Websocket    
        https://blog.csdn.net/pacosonswjtu/article/details/51914567
### Step 13th maven调试配置
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
	
### Step 14th 版本配合
* springcloud Finchley.RELEASE 配合 springframework.boot 2.0.3.RELEASE 版本
* springcloud Greenwich.RELEASE 配合 springframework.boot 2.1.3.RELEASE 版本
    
    否则会出现抽象函数空的问题。
    
REF https://blog.csdn.net/forezp/article/details/81040925

## Step 15th 基于Stomp/SockJS的WebSocket
* 增加依赖
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
```
    注：实际上还要导入spring-messaging 

* 使用配置类方式进行配置 WebSocketConfig

```
    @Configuration
    @EnableWebSocketMessageBroker //这个配置类不仅配置了 WebSocket，还配置了基于代理的 STOMP 消息
    public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

```
        注意此处扩展了AbstractWebSocketMessageBrokerConfigurer，其中的几个函数
   
```
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //定义了一个客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息
        config.enableSimpleBroker("/topic","/user");

        config.setUserDestinationPrefix("/user/");
        //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀
        config.setApplicationDestinationPrefixes("/center");

    }
    
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

// 添加一个服务端点，来接收客户端的连接。将 “/websocket” 路径注册为 STOMP 端点。
// 这个路径与之前发送和接收消息的目的路径有所不同， 这是一个端点，
// 客户端在订阅或发布消息到目的地址前，要连接该端点，
// 即用户使用sockjs发送请求 ：url=’http://127.0.0.1:8762/websocket 与 STOMP server 进行连接，之后再转发到订阅url
// withSockJS作用是添加SockJS支持
// setAllowedOrigins("*") 是保证比如127.0.0.1/websocket.html 这种不同domain 的代码也可以访问127.0.0.1:8762的web socket

        registry.addEndpoint("/websocket").setHandshakeHandler(new WebSocketHandshakeHandler()).setAllowedOrigins("*").withSockJS();
    }
    
//    添加对WebSocket的一些配置
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        
        container.setMaxTextMessageBufferSize(8192*4);
        container.setMaxBinaryMessageBufferSize(8192*4);//比如二进制图片、音频文件

        return container;
    }
    //客户化JSON的反串行化插件
   
    @Override
    public boolean configureMessageConverters(
            List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(smt);
        //允许JSON使用单引号进行字段标注
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);

        converter.setObjectMapper(objectMapper);
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(new StringMessageConverter());
        messageConverters.add(new ByteArrayMessageConverter());
        messageConverters.add(converter);
        return false;
    }

```   
* 配置消息处理器MessageController
```

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate msgTemplate;
// @MessageMapping 标识客户端发来消息的请求地址，前面我们全局配置中制定了服务端接收的地址以“/center”开头，
// 所以客户端发送消息的请求连接是：/center/message；
    @MessageMapping("/message")
    @SendToUser("/topic/message") //可以将消息只返回给发送者
    //@SendTo("/topic/message") //会将消息广播给所有订阅/message这个路径的用户
    public JSONObject chat(JSONObject message, Principal user) throws Exception {
        return JSONObject.parseObject("{responseto:"+message.toJSONString()+"}");
    }
    //定向发送
    @RequestMapping(value = "/msg/send/{username}/{message}")
    public String sendTo(@PathVariable  String username,@PathVariable  String message)
    {
        msgTemplate.convertAndSendToUser(username, "/topic/message",message);
        return "success";
    }
    //群聊
    @MessageMapping("/group/{groupId}")
    public void groupMessage(String message, @DestinationVariable String groupId){
        String dest = "/topic/" + groupId + "/" + "message";
        msgTemplate.convertAndSend(dest, message);
    }
}
```

* 客户端javascript 代码
  * websocket-spring-test.js

```javascript
(function() {
var springWs = {
    socket:null,
    stompClient:null,
    status:0,
    status_idle:0,
    status_connected:1,
    wsEndPoint:'http://127.0.0.1:8762/websocket',
    wsSubscribePoint:'/user/topic/message',
    wsBroadcastPoint:'/topic/message',
    wsRequestPoint:"/center/message",
    retryCount:0,
    heartBeatTimer:null,
    heartBeatMissed:0,
    recentHeartBeat:0,
    connect:function()
    {
        if(springWs.socket==null || springWs.socket.readyState==SockJS.CONNECTING||springWs.socket.readyState==SockJS.CLOSED ){

            springWs.socket= new SockJS(springWs.wsEndPoint,null,{transports:"websocket"});
            springWs.stompClient = Stomp.over(springWs.socket);
            springWs.stompClient.connect({}, springWs.connectCallback,springWs.connectErrorCallback);
            springWs.retryCount=springWs.retryCount+1;
            springWs.socket.onheartbeat = function() {
                springWs.recentHeartBeat= Date.parse(new Date());
                springWs.stopHeartBeat();
            };
        }
    },
    connectCallback:function(frame) {

      console.log('Connected: ' + frame);
      springWs.retryCount=0;
      var uid=cookieHandle.getCookie('cookie_user_id');
      springWs.stompClient.subscribe(springWs.wsSubscribePoint, function (message) {
          springWs.dispatchMessage(message.body);
      });
      springWs.stompClient.subscribe(springWs.wsBroadcastPoint, function (message) {
          springWs.dispatchMessage(message.body);
      });
      springWs.shakehand();
    },
    shakehand:function(){
        var uid=cookieHandle.getCookie('cookie_user_id');
        var sid=cookieHandle.getCookie('ci_session_id');
        springWs.send("{'action':'hello','uid':'"+uid+"','sid':'"+sid+"'}");
    },
    connectErrorCallback:function(frame)
    {

        if(springWs.retryCount>4)
        {
            var jsonMessage={'action':'error','message':'连接服务器错误'};
            console.log('error: ' + frame);
            springWs.dispatchMessage(JSON.stringify(jsonMessage));
        }
        else {
            setTimeout(springWs.connect,5000);//隔5秒重新连接一次
        }
    },
    disconnect:function() {
        if (springWs.stompClient != null) {
            springWs.stompClient.disconnect(function(){
                console.log("Disconnected");
            });
        }
    },
    startHeartBeat:function(){
        springWs.heartBeatTimer=setInterval(springWs.heartBeatCheck,10000);
    },
    stopHeartBeat:function(){
        clearInterval(springWs.heartBeatTimer);
    },
    heartBeatCheck:function(){
        var now = Date.parse(new Date());
        var diff=now-springWs.recentHeartBeat;
        console.log("heartBeatCheck:"+diff);
        if(diff>=30000) //超过30秒没有收到任何数据
        {
            springWs.close();
            setTimeout(springWs.connect,1000);//1秒后连接一次
            springWs.stopHeartBeat();
        }
    },
    close:function(){
        springWs.disconnect();
        springWs.dispatchMessage(JSON.stringify({action:'close'}));
    },
    dispatchMessage:function(jsonMessage)
    {
        console.log("recevied:"+jsonMessage);
    },
    send:function(message)
    {
        springWs.stompClient.send(springWs.wsRequestPoint, {}, message);
    },
    dispatchMessage:function(message)
    {
      console.log((new Date()) + ' received message: ' + message);
      springWs.recentHeartBeat=Date.parse(new Date());
      console.log("update heartbeat:"+springWs.recentHeartBeat);
      var json=JSON.parse(message);
      switch(json.action)
      {
            case 'hello' ://握手
            
           break;
        //    case 'heartBeat':
        //    springWs.heartBeatAck();
          default:
          break;
      }

    }

}
if (typeof exports !== "undefined" && exports !== null) {
  exports.springWs = springWs;
}

if (typeof window !== "undefined" && window !== null) {

  window.springWs = springWs;
} else if (!exports) {
  self.springWs = springWs;
}
    window.websocket=springWs;
}).call(this);
```
* * socketjs.min.js
  * stomp.min.js
  * test.html
```
            <head>
                <script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
                <script src="https://cdn.bootcss.com/sockjs-client/1.1.4/sockjs.min.js"></script>
                <script src="https://cdn.bootcss.com/stomp.js/2.3.3/stomp.min.js"></script>
                <script src="js/websocket-spring-test.js"></script>
            
                <script type="text/javascript">
                    $(document).ready(function($) {
                        websocket.connect();
                    });
                </script>
            </head>

```  