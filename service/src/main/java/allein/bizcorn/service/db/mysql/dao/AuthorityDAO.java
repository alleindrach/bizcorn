package allein.bizcorn.service.db.mysql.dao;

import allein.bizcorn.model.entity.Authority;
import org.springframework.cache.annotation.Cacheable;

@org.apache.ibatis.annotations.Mapper
public interface AuthorityDAO extends BaseDAO<Authority> {
    @org.apache.ibatis.annotations.Select("select id,user_id,authority from authorities where id = #{id}")
    @Cacheable(value="auth", keyGenerator = "KeyGenerator")
    Authority selectByIdCached(int id);

    @org.apache.ibatis.annotations.Select("select id,user_id,authority from authorities where id = #{id}")
    Authority selectById(int id);

    @org.apache.ibatis.annotations.Insert(" insert into authorities (id,user_id,authority  )  values (#{id}, #{user_id}, #{authority})")
    int insert(Authority authority);

}
