package allein.service.mapper;

import allein.model.user.User;
@org.apache.ibatis.annotations.Mapper
public interface UserMapper extends BaseMapper<User> {
    User selectById(int id);
    int insert(User user);
    int update(User user);
    User selectByName(String name);
}
