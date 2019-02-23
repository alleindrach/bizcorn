1 代码结构
    bizcorn根目录的pom.xml是根项目，其设定了依赖项的版本。
    eurekaserver为服务发现
    service为eureka的client
    
1 调试
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
	
2 版本配合
    注意springcloud Finchley.RELEASE 配合 springframework.boot 2.0.3.RELEASE 版本
    注意springcloud Greenwich.RELEASE 配合 springframework.boot 2.1.3.RELEASE 版本
    
    否则会出现抽象函数空的问题。
    
    
        