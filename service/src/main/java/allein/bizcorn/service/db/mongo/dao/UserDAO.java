package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.User;

public interface UserDAO extends  BaseDAO<User> {
    allein.bizcorn.model.mongo.User selectByName(String name);
    User selectByMobile(String mobile);
    User select(String idNameMobile);
}
