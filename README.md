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
	
