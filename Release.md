2019-06-27
修改编译目标参数，编译成可执行文件
例如，编译运行 service
cd service
mvn package
cp service-1.0.conf target/
./target/service-1.0.jar

其中，service-1.0.conf 是同名jar的配置文件，里面的JAVA_OPTS是java jvm的配置参数


参考 https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-script-customization-conf-file


2019-05-10
1 Feign Header 穿透问题
入向：client---request1-->feign.encoder--request2--->service
这里的request1.headers向request2.headers的复制，通过SessionReplicateInterceptor 进行。
出向:service---response1-->feign.decoder---response2-->client
这里的response1.headers向resposne2的复制，通过自定义的decoder进行。

2 IResultor接口
用于从mongodb实体类向输出JSON对象的转化，比如说User的打码，
另外在JSON转化过程中可能出现无限递归的情况，要使用FastJson的@JsonField(Serialize=false)来标注
比如说User.curPartner 字段，也是一个User的引用，这样会导致无限递归。



2019-05-09
1 用户Users 的authorities字段变为普通的字符串数组：
更新脚本：
db.Users.update(
    {},
      { $set: { "Authorities" : ["ROLE_USER"] } }
   );
增加权限脚本
db.Users.updateOne(
    {"username":"Allein")},
      { $push: { "Authorities" : "ROLE_ADMIN" } }
   );

2 docker 的mongodb数据文件目录映射到宿主机
    创建宿主机Docker Mongodb 数据目录
    mkdir -p /data/docker/mongodb
    也可以修改docker-compose.yml 的volume 数据
    另外，mac机器需要修改docker preferences share 目录，加入data目录