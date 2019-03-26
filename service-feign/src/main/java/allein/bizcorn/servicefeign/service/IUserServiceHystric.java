package allein.bizcorn.servicefeign.service;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class IUserServiceHystric implements IUserService {
    @Override
    public Result<User> login(@RequestParam(value = "name") String name, @RequestParam(value = "password") String password)
    {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result<User> logout() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }


    @Override
    public Result<User> update(String mobile, Long userId, HttpSession session, HttpServletRequest request) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));

    }

    @Override
    public void captcha(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public Result mobileCaptcha(String mobile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }
}
