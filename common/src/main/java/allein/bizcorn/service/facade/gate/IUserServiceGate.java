/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade.gate;

import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface IUserServiceGate {
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    /*
    @Description:用户登录，具体的处理见security中的CustomAuthenticationSuccessHandler 和CustomAuthenticationFailureHandler
    @Param:[username, password, captcha]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:12:04 PM
    */
    Result login(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "captcha", required = false) String captcha);


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    Result logout();

    @RequestMapping(value = "/user")
    /*
    @Description:用户更新
    @Param:[mobile]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:2:42 PM
    */
    Result update(
            @RequestParam(value = "mobile") String mobile
    );

    @RequestMapping(value="/user/update")
    /*
    @Description:更新用户
    @Param:[user]
    @Return:allein.bizcorn.model.output.Result<java.lang.Integer>
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:2:47 PM
    */
    public Result<Integer> update(@RequestParam User user);

    @RequestMapping("/user/homepage")
    public Result<IUser> fetchHomepage();

    @RequestMapping(value = "/register")
    /*
    @Description: 普通用户注册
    @Param:[
    username, password, captcha, mobile]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:11:59 AM
    */
    Result register(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mobileCaptcha") String captcha,
            @RequestParam(value = "mobile") String mobile,
            @CookieValue(value = SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME) String mobileCaptchaKey
    );

    @RequestMapping(value = "/kid/register/{mac}")
    /*
    @Description:小童注册
    @Param:[mac mac地址，清除字段间的间隔，00:11:22:33==>00112233]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:11:59 AM
    */
    Result register(
            @PathVariable(value = "mac") String mac
    );

    @RequestMapping(value = "/user/bind/{mac}")
    /*
    @Description:绑定小童，发起绑定者可以是普通用户，也可以是小童
    @Param:[mac]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:12:02 PM
    */
    Result bind(
            @PathVariable(value = "mac") String mac
    );

    @RequestMapping(value = "/password/reset")
    /*
    @Description: 重置密码
    @Param:[
    password, captcha, mobile]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:11:59 AM
    */
    Result resetPassowrd(
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mobileCaptcha") String captcha,
            @RequestParam(value = "mobile") String mobile,
            @CookieValue(value = SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME) String mobileCaptchaKey
    );

}
