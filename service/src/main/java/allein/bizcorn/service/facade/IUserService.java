package allein.bizcorn.service.facade;

import allein.bizcorn.common.model.entity.user.User;

public interface IUserService {
    User getUserByUsername(String userName);
    User getUserByMobile(String mobile);
    Long getUserLoginErrorTimes(String userName);
    Long incUserLoginErrorTimes(String userName);
    Boolean rstUserLoginErrorTimes(String userName);
    int updateUser(User user);
}

