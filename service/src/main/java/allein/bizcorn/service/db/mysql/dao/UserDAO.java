package allein.bizcorn.service.db.mysql.dao;

import allein.bizcorn.model.entity.User;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface UserDAO extends BaseDAO<User> {
    @org.apache.ibatis.annotations.Select("select id,username,password,enabled,mobile  from users where id = #{id}")
    @Cacheable(value="user", keyGenerator = "UserKeyGenerator")
    User selectByIdCached(int id);

    @org.apache.ibatis.annotations.Select("select id,username,password,enabled,mobile  from users where id = #{id}")
    User selectById(int id);

    @org.apache.ibatis.annotations.Insert(" insert into users (id,username,password,enabled,mobile  )  values (#{id}, #{username}, #{password},#{enabled})")
    int insert(User user);

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

    @org.apache.ibatis.annotations.Select("select id,username,password,enabled,mobile from  users where username = #{username}")
    @Cacheable(value="user",keyGenerator = "UserKeyGenerator")
    User selectByNameCached(String name);

    @org.apache.ibatis.annotations.Select("select id,username,password,enabled,mobile from users where username = #{username}")
    User selectByName(String name);

    @org.apache.ibatis.annotations.Select("select id,username,password,enabled,mobile from users where mobile = #{mobile}")
    User selectByMobile(String mobile);




}
