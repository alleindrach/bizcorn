package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.util.SecurityUtil;

import allein.bizcorn.model.facade.IAuthority;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.Authority;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.security.UserDetails;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    public
    @PreAuthorize("hasRole('USER')")
    @Transactional
//    @AuthLogin(injectUidFiled = "userId")
    Result<IUser> update(
            @RequestParam(value = "mobile") String mobile,
            HttpSession session,
            HttpServletRequest request
    ) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(usernamePasswordAuthenticationToken !=null &&
                usernamePasswordAuthenticationToken.isAuthenticated() &&
                usernamePasswordAuthenticationToken.getPrincipal() instanceof  UserDetails &&
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
    public Result<IUser> getUserByUsername(String userName) {

        User user=userDAO.selectByName(userName);
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
    public Result<IUser> getUserByMobile(String mobile)
    {
        return Result.successWithData(mobile);
    }

    @Override
    public Result<List<String>> getUserAuthorities(String userId) {
        User user=userDAO.selectById(userId);
        List<String> auths=new ArrayList<>();
        for (Authority auth:user.getAuthoritys()
             ) {
            auths.add(auth.getAuthority());
        }
        return Result.successWithData(auths);
    }


    @RequestMapping("/user/new")
    public Result<IUser> addUser(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "authority") String authority)
    {
    
        User newuser=new User();
        newuser.setUsername(username);
        newuser.setPassword( DigestUtils.md5DigestAsHex(password.toString().getBytes()));
        Authority authorityx=  new Authority();
        authorityx.setAuthority(authority);
        HashSet<Authority> auths=new HashSet<Authority>();
        auths.add(authorityx);
        newuser.setAuthoritys(auths);
        userDAO.save(newuser);
        return  Result.successWithData(newuser);
    }
}
