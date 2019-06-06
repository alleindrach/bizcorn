/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade.gate;

import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.Profile;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @RequestMapping(value = "/user/profile/set")
    /*
    @Description:用户更新
    @Param:[profile:{
    nickName:xxx,
    avatar:fileid,
    birthDate:number,
    sex:M|F,
    }]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:2:42 PM
    */
    Result updateProfile(
            @RequestBody  JSONObject profile
    );
    @RequestMapping(value="/user/profile/get")
    public Result<Profile> getProfile();


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
/*
@Description:生成一个绑定的凭据
@Param:
@Return:
@Author:Alleindrach@gmail.com
@Date:2019/5/13
@Time:8:51 AM
*/
    @RequestMapping(value = "/user/bind/token")
    Result newBindToken();
    
    @RequestMapping(value = "/user/bind/fire/{token}")
    /*
    @Description:绑定小童，发起绑定者可以是普通用户，也可以是小童
    @Param:[mac]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:12:02 PM
    */
    Result firebind(
            @PathVariable(value = "token") String token
    );
    @RequestMapping(value = "/user/bind/confirm/{token}")
    /*
    @Description:绑定小童，发起绑定者可以是普通用户，也可以是小童
    @Param:[mac]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:12:02 PM
    */
    Result confirmBind(
            @PathVariable(value = "token") String token
    );

    @RequestMapping(value = "/user/bind/query/{token}")
    /*
    @Description:绑定小童，发起绑定者可以是普通用户，也可以是小童
    @Param:[mac]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/4/30
    @Time:12:02 PM
    */
    Result queryBind(
            @PathVariable(value = "token") String token
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
    @RequestMapping(value = "/password/change")
    Result changePassowrd(
            @RequestParam(value = "password") String password,
            @RequestParam(value = "oldPassword") String oldPassword
    );
//    @RequestMapping(value = "/admin/user/summary")
//    Result adminUserListSummary(
//            @RequestBody JSONObject filter
//    );
    @RequestMapping(value = "/admin/user/list")
    Result adminUserList(
            @RequestBody JSONObject params);
    @RequestMapping(value = "/admin/user/authorities/update")
    Result adminChangeUserAuthorities(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "authorities") String authorities);

    @RequestMapping(value = "/admin/user/update",consumes={ "application/json", "text/plain" },produces = {"application/json"})
    Result adminUpdateUser(
            @RequestBody JSONObject jsoUser);

    @RequestMapping(value = "/admin/user/add",consumes={ "application/json", "text/plain" },produces = {"application/json"})
    Result adminAddUser(
            @RequestBody JSONObject jsoUser);

    @RequestMapping(value = "/admin/user/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = {"application/json"})
    Result adminImportUser(
            @RequestPart MultipartFile file) throws Exception;

}

