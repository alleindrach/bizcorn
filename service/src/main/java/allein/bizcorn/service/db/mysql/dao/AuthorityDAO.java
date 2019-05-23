package allein.bizcorn.service.db.mysql.dao;

import org.springframework.cache.annotation.Cacheable;

@org.apache.ibatis.annotations.Mapper
public interface AuthorityDAO extends BaseDAO<String> {
    @org.apache.ibatis.annotations.Select("select id,user_id,authority from authorities where id = #{id}")
    @Cacheable(value="auth", keyGenerator = "KeyGenerator")
    String selectByIdCached(int id);

    @org.apache.ibatis.annotations.Select("select id,user_id,authority from authorities where id = #{id}")
    String selectById(int id);

    @org.apache.ibatis.annotations.Insert(" insert into authorities (id,user_id,authority  )  values (#{id}, #{user_id}, #{authority})")
    int insert(String authority);

}
