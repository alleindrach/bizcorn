package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.entity.User;
import allein.bizcorn.model.output.Result;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class UserServiceHystric implements  UserServiceProxy{
    @Override
    public Result<User> login(String username, String password, String captcha) 
    {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result<User> logout() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }


    public Result<User> update(String mobile, HttpSession session, HttpServletRequest request) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }


    @Override
    public Result<User> getUserByUsername(String userName) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }

    @Override
    public Result<Long> getUserLoginErrorTimes(String userName) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }

    @Override
    public Result<Long> incUserLoginErrorTimes(String userName) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }

    @Override
    public Result<Boolean> rstUserLoginErrorTimes(String userName) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }

    @Override
    public Result<Integer> updateUser(User user) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }

    @Override
    public Result<User> getUserByMobile(String mobile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }


}
