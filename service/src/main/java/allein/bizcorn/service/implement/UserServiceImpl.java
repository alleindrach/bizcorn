package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.service.dao.UserDAO;
import allein.bizcorn.service.facade.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    ICacheAccessor cacheAccessor;
    @Autowired
    UserDAO userDAO;
    @Override
    public User getUserByUsername(String userName) {
        return userDAO.selectByName(userName);
    }
    @Value("${bizcorn.user.login.errortimes.cache.key.prefix}")
    String ErrorTimesKey="user_login_error_times_cache_";
    @Override
    public Long getUserLoginErrorTimes(String userName) {
        return cacheAccessor.getLong(this.ErrorTimesKey+userName);
    }
    @Override
    public Long incUserLoginErrorTimes(String userName)
    {
        return cacheAccessor.inc(this.ErrorTimesKey+userName);
    }
    @Override
    public Boolean rstUserLoginErrorTimes(String userName)
    {
        return cacheAccessor.del(this.ErrorTimesKey+userName);
    }
    @Override
    public int updateUser(User user)
    {
        return userDAO.update(user);
    }
    @Override
    public User getUserByMobile(String mobile)
    {
        return userDAO.selectByMobile(mobile);
    }
}
