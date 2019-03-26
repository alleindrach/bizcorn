package allein.bizcorn.service.dao;

import allein.bizcorn.common.model.entity.user.User;
import org.springframework.cache.annotation.Cacheable;

@org.apache.ibatis.annotations.Mapper
public interface UserDAO extends BaseDAO<User> {
    @org.apache.ibatis.annotations.Select("select id,name,login_password ,login_password_random, mobile from tad_user where id = #{id}")

    @Cacheable(value="user", keyGenerator = "UserKeyGenerator")
    User selectByIdCached(int id);

    @org.apache.ibatis.annotations.Select("select id,name,login_password ,login_password_random, mobile from tad_user where id = #{id}")
    User selectById(int id);

    @org.apache.ibatis.annotations.Insert(" insert into tad_user (id, name, login_password,mobile )  values (#{id}, #{name}, #{loginPassword},#{mobile})")
    int insert(User user);

    @org.apache.ibatis.annotations.Update(
            {"<script>",
                    "update tad_user" ,
                            "<set >" ,
                            " <if test='name != null' > " ,
                            "   name = #{name,jdbcType=VARCHAR}," ,
                            " </if>" ,
                            " <if test='loginPassword != null' >" ,
                            "    login_password = #{loginPassword,jdbcType=VARCHAR}," ,
                            " </if>" ,
                            " <if test= 'mobile != null' > " ,
                            "    mobile = #{mobile,jdbcType=VARCHAR} " ,
                            " </if>" ,
                            "</set>" ,
                            "where id = #{id,jdbcType=INTEGER}",
                    "</script>"})
    int update(User user);

    @org.apache.ibatis.annotations.Select("select id,name,login_password ,login_password_random, mobile from tad_user where name = #{name}")
    @Cacheable(value="user",keyGenerator = "UserKeyGenerator")
    User selectByNameCached(String name);

    @org.apache.ibatis.annotations.Select("select id,name,login_password , login_password_random,mobile from tad_user where name = #{name}")
    User selectByName(String name);

    @org.apache.ibatis.annotations.Select("select id,name,login_password , login_password_random,mobile from tad_user where mobile = #{mobile}")
    User selectByMobile(String mobile);
}
