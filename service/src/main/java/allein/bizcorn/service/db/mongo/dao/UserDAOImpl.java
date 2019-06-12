package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.User;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.*;
@Component
public class UserDAOImpl extends   BaseDAOImpl<User> implements UserDAO  {

    @Override
    public User selectByName(String name) {
        return this.mongoTemplate.findOne(new Query(where("username").is(name)),User.class);
    }

    @Override
    public User selectByMobile(String mobile) {
        return this.mongoTemplate.findOne(new Query(where("mobile").is(mobile)),User.class);
    }

    @Override
    public User select(String idNameMobile) {
        User user=null;
        user=this.selectByName(idNameMobile);
        if(user!=null) return user;
        user=this.selectByMobile(idNameMobile);
        if(user!=null) return user;
        user=this.get(idNameMobile);
        if(user!=null) return user;
        return user;
    }

}
