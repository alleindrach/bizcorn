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