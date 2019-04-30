package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;


import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
public class UserServiceHystric implements  UserServiceProxy{


    @Override
    public Result<IUser> login(String username, String password, String captcha) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result logout() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }


    public Result update(String mobile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }


    @Override
    public Result<IUser> getUserByUsername(String userName) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result<IUser> getMaskedUserByUsername(String userName) {
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
    public Result<Integer> update(User user) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result<IUser> getUserByMobile(String mobile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result<List<String>> getUserAuthorities(String userId) {
        return null;
    }

    @Override
    public Result<IUser> fetchHomepage() {
        return null;
    }

    @Override
    public Result<IUser> register( String username, String password, String captcha, String mobile,String mobileCaptchaKey) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result register(String mac) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result bind(String mac) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }


}
