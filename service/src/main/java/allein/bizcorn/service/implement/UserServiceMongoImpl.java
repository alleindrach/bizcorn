package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;

import allein.bizcorn.common.util.Masker;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.Authority;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.captcha.CaptchaResult;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.security.config.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

    @Override
    public Result<IUser> login(String username, String password, String captcha) {
        return null;
    }

    @Override
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null){
//            new SecurityContextLogoutHandler().logout(request, response, auth);
//        }
        return Result.successWithMessage("log out!");
    }

    public
//    @PreAuthorize("hasRole('USER')")
//    @Transactional
//    @AuthLogin(injectUidFiled = "userId")
    Result<IUser> update(
            @RequestParam(value = "mobile") String mobile
    ) {
        logger.info("session_id>>>>>{}",RequestContextHolder.getRequestAttributes().getSessionId());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(usernamePasswordAuthenticationToken !=null &&
                usernamePasswordAuthenticationToken.isAuthenticated() &&
                usernamePasswordAuthenticationToken.getPrincipal() instanceof UserDetails &&
                ((UserDetails) usernamePasswordAuthenticationToken.getPrincipal()).getUsername()!=null)
        {
            String username=((UserDetails) usernamePasswordAuthenticationToken.getPrincipal()).getUsername();
            if(username!=null)
            {
                userDAO.update(new Query(Criteria.where("username").is(username)),new Update().set("mobile",mobile));
                return Result.successWithData(userDAO.selectByName(username));
            }
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
    public Result<Integer> updateUser(IUser user)
    {
        return Result.successWithData(user);
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



    public Result<IUser> register(
           HttpServletRequest request,
           String username,
           String password,
           String captcha,
           String mobile)
    {
    
        User newuser=new User();
        newuser.setUsername(username);
        String mobileCaptchaKey=null;
        for ( Cookie cookie:request.getCookies()
             ) {
            if(cookie.getName().compareToIgnoreCase(SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME)==0)
            {
                mobileCaptchaKey=cookie.getValue();
            }
        }
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
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/user/homepage")
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
