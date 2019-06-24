package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;


import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.Profile;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public Result updateProfile(JSONObject profile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result<Profile> getProfile() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result updateKidProfile(JSONObject profile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result<Profile> getKidProfile() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result getSelfInfo() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result getFriends() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result bindFriend(String id) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    public Result update(String mobile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
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
    public Result newBindToken() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result firebind(String mac) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result confirmBind(String token) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result queryBind(String token) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result setElderNumbers(JSONObject params) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result getElderNumbers() {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result resetPassowrd(String password, String captcha, String mobile, String mobileCaptchaKey) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result changePassowrd(String password, String oldPassword) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminUserList(JSONObject params) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminChangeUserAuthorities(String id, String authorities) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminUpdateUser(JSONObject user) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminAddUser(JSONObject jsoUser) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminImportUser(MultipartFile files) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }


}
