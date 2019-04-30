package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;

import allein.bizcorn.common.util.Masker;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.Authority;
import allein.bizcorn.model.mongo.Kid;
import allein.bizcorn.model.mongo.Role;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.model.security.CaptchaResult;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RefreshScope
//@RequestMapping("/mongo")
public class UserServiceMongoImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceMongoImpl.class);

    @Value("${bizcorn.session.prefix}")
    String sessionPrefix;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @Value("${bizcorn.session.attribute.timeout}")
    String sessionAttrTimeout;

    @Value("${bizcorn.session.timeout}")
    Long sessionTimeout;

    @Autowired
    ICacheAccessor cacheAccessor;

    @Autowired
    UserDAO userDAO;
    @Autowired
    private CaptchaImageHelper captchaImageHelper;

    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;

    private User getUserFromSession(){
        String username= SecurityUtil.getUserName();
        if(username!=null)
        {
            return userDAO.selectByName(username);

        }else
        {
            return null;
        }
    }
    @Override
    public Result login(String username, String password, String captcha) {
        return null;
    }

    @Override
    public Result logout() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null){
//            new SecurityContextLogoutHandler().logout(request, response, auth);
//        }
        return Result.successWithMessage("log out!");
    }

    public
    @PreAuthorize("hasRole('USER')")
//    @Transactional
//    @AuthLogin(injectUidFiled = "userId")
    Result update(
            @RequestParam(value = "mobile") String mobile
    ) {
        logger.info("session_id>>>>>{}",RequestContextHolder.getRequestAttributes().getSessionId());
        String username=SecurityUtil.getUserName();
        if(username!=null){
                userDAO.update(new Query(Criteria.where("username").is(username)),new Update().set("mobile",mobile));
                return Result.successWithData(this.getMaskedUserByUsername(username).getData());
        }

        return  Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_ID_INVALID));
    }

    @Override
    public Result<IUser> getUserByUsername(@PathVariable("username") String userName) {

        User user=userDAO.selectByName(userName);
        return Result.successWithData(user);

    }
    @Override
    public Result<IUser> getMaskedUserByUsername(@PathVariable("username") String userName) {

        User user=userDAO.selectByName(userName);
        user.setMobile(Masker.getMaskCharWay(user.getMobile(),3,9));
        user.setPassword("");
        return Result.successWithData(user);

    }
    private User getMaskedUser(User userOri)
    {
        User user=userOri;
        user.setMobile(Masker.getMaskCharWay(user.getMobile(),3,9));
        user.setPassword("");
        return user;
    }
    @Value("${bizcorn.user.login.errortimes.cache.key.prefix}")
    String ErrorTimesKey="user_login_error_times_cache_";
    @Override
    public Result<Long> getUserLoginErrorTimes(String userName)
    {
        return Result.successWithData(cacheAccessor.getLong(this.ErrorTimesKey+userName));
    }
    @Override
    public Result<Long> incUserLoginErrorTimes(String userName)
    {
        return Result.successWithData(cacheAccessor.inc(this.ErrorTimesKey+userName));
    }
    @Override
    public Result<Boolean> rstUserLoginErrorTimes(String userName)
    {
        return Result.successWithData(cacheAccessor.del(this.ErrorTimesKey+userName));
    }
    @Override
    public Result<Integer> update(User user)
    {
        userDAO.save(user);
        return Result.successWithData(user.getId());
    }
    @Override
    public Result<IUser> getUserByMobile(@PathVariable("mobile") String mobile)
    {
        return Result.successWithData(mobile);
    }

    @Override
    public Result<List<String>> getUserAuthorities(@PathVariable("id") String userId) {
        User user=userDAO.get(userId);
        List<String> auths=new ArrayList<>();
        for (Authority auth:user.getAuthorities()
             ) {
            auths.add(auth.getAuthority());
        }
        return Result.successWithData(auths);
    }


    @Override
    public Result register(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mobileCaptcha") String captcha,
            @RequestParam(value = "mobile") String mobile,
            @CookieValue(value= SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME) String mobileCaptchaKey
    )
    {
    
        User newuser=new User();
        newuser.setUsername(username);

        CaptchaResult captchaResult=captchaMessageHelper.checkCaptcha(mobileCaptchaKey,captcha,mobile,SecurityConstants.SECURITY_KEY,true);
        if(!captchaResult.isSuccess())
            Result.failWithException(new CommonException(ExceptionEnum.CAPTCH_INVALID));

        newuser.setPassword( DigestUtils.md5DigestAsHex(password.toString().getBytes()));
        newuser.setMobile(mobile);

        HashSet<Authority> auths=new HashSet<Authority>();
        auths.add(new Authority().setAuthority("ROLE_USER"));
        newuser.setAuthorities(auths);
        userDAO.save(newuser);
        return  Result.successWithData(newuser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Result register(@PathVariable("mac")  String mac) {

        if(userDAO.selectByName(mac)!=null){
            return Result.failWithException(new CommonException(ExceptionEnum.USER_EXISTS));
        }

        Kid kid=new Kid();
        kid.setUsername(mac);
        String mobileCaptchaKey=null;

        kid.setPassword( DigestUtils.md5DigestAsHex(mac.toString().getBytes()));
        HashSet<Authority> auths=new HashSet<Authority>();
        auths.add(new Authority().setAuthority("ROLE_USER"));
        kid.setAuthorities(auths);
        try {
            userDAO.save(kid);
        }catch(DuplicateKeyException exception)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_REGISTER_FAIL));
        }
        return  Result.successWithData(getMaskedUser(kid));
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result  bind(
            @PathVariable(value = "mac") String mac
    ){
        Kid kid= (Kid) userDAO.selectByName(mac);
        if(kid==null){
            return Result.failWithException(new CommonException(ExceptionEnum.USER_KID_ACCOUNT_NOT_EXIST));
        }
        if(kid.getRole()!= Role.KID)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_KID_INVALID));
        }
        User userBinding=getUserFromSession();
        if(userBinding==null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST));
        }
        boolean isDirt=false;
        switch (userBinding.getRole())
        {
            case KID:
                if(kid.isCanBind()){
                    userBinding.setCurPartner(kid);
                    kid.setCurPartner(userBinding);
                    isDirt=true;
                }else
                {
                    return Result.failWithException(new CommonException(ExceptionEnum.BIND_KID_STATUS_INVALID));
                }
                break;
            default:
                if(kid.getParent()==null)
                {
                    kid.setParent(userBinding);
                    kid.setCurPartner(userBinding);
                    userBinding.setCurPartner(kid);
                    isDirt=true;
                }else
                {
                    if(kid.isValidElder(userBinding.getMobile()))
                    {
                        kid.setCurPartner(userBinding);
                        userBinding.setCurPartner(kid);
                        isDirt=true;
                    }
                    else
                    {
                        return Result.failWithException(new CommonException(ExceptionEnum.BIND_USER_INVALID));
                    }
                }
                break;
        }

        if(isDirt)
        {
            try {
                userDAO.save(userBinding);
                userDAO.save(kid);
            }
            catch(Exception ex){
                logger.error("绑定错误",ex);
                return  Result.failWithException(new CommonException(ExceptionEnum.BIND_SAVE_ERROR));
            }
        }
        return Result.successWithMessage("绑定成功");
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public Result<IUser> fetchHomepage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth.getPrincipal() instanceof UserDetails )
//        {
//            try {
//                UserDetails userDetails = (UserDetails) auth.getPrincipal();
//                User user = userDAO.selectByName(userDetails.getUsername());
//                return Result.successWithData(user);
//            }catch(Exception ex) {
//                return Result.failWithException(ex);
//            }
//        }
        return  Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
    }

}
