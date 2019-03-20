package allein.service.dao;
import allein.model.data.user.User;
@org.apache.ibatis.annotations.Mapper
public interface UserDAO extends BaseDAO<User> {
    @org.apache.ibatis.annotations.Select("select id,name,login_password , mobile from tad_user where name = #{id}")
    User selectById(int id);
    @org.apache.ibatis.annotations.Select(" insert into tad_user (id, name, login_password,mobile )  values (#{id}, #{name}, #{loginPassword},#{mobile})")
    int insert(User user);
    @org.apache.ibatis.annotations.Select("update tad_user" +
            "<set >" +
            " <if test=\"name != null\" > " +
            "   name = #{name,jdbcType=VARCHAR}," +
            " </if>" +
            " <if test=\"loginPassword != null\" >" +
            "    login_password = #{loginPassword,jdbcType=VARCHAR}," +
            " </if>" +
            " <if test=\"mobile != null\" > " +
            "    mobile = #{mobile,jdbcType=VARCHAR}, " +
            " </if>" +
            "</set>" +
            "where id = #{id,jdbcType=INTEGER}")
    int update(User user);
    @org.apache.ibatis.annotations.Select("select id,name,login_password , mobile from tad_user where name = #{name}")
    User selectByName(String name);
}
