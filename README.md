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