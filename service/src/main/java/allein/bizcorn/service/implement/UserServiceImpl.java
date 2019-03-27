package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.captcha.CaptchaResult;
import allein.bizcorn.service.dao.UserDAO;
import allein.bizcorn.service.security.SimpleAuthenticationManager;
import allein.bizcorn.service.security.config.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RefreshScope
public class UserServiceImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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
    private SimpleAuthenticationManager simpleAuthenticationManager;

    @Autowired
    private CaptchaImageHelper captchaImageHelper;

    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;

    public
    @PreAuthorize("hasRole('USER')")
    @Transactional
//    @AuthLogin(injectUidFiled = "userId")
    Result<User> update(
            @RequestParam(value = "mobile") String mobile,
            HttpSession session,
            HttpServletRequest request
    ) {
        logger.info(" {} >>> {}",  request.getRequestURL().toString(),session.getId());
        String username= SecurityUtil.getUserName();
        String test=(String)session.getAttribute("username");
        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        } else {
            user.setMobile(mobile);
            int updated=userDAO.update(user);
            if(updated>0)
            {
                return new Result<User>(1, "", null, user);
            }else
            {
                return new Result<User>(0, "", null, user);
            }
        }
    }

    @Override
    public Result<User> getUserByUsername(String userName) {
        return Result.successWithData( userDAO.selectByName(userName));
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
    public Result<Integer> updateUser(User user)
    {
        return Result.successWithData(userDAO.update(user));
    }
    @Override
    public Result<User> getUserByMobile(String mobile)
    {
        return Result.successWithData(userDAO.selectByMobile(mobile));
    }
}
