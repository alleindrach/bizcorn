package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.entity.User;
import allein.bizcorn.model.facade.IAuthority;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.db.mysql.dao.UserDAO;
import allein.bizcorn.service.security.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

//@RestController
@RefreshScope
//@RequestMapping("/mysql")
public class UserServiceMysqlImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceMysqlImpl.class);

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
                return new Result<IUser>(1, "", null, user);
            }else
            {
                return new Result<IUser>(0, "", null, user);
            }
        }
    }

    @Override
    public Result<IUser> getUserByUsername(String userName) {
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
    public Result<Integer> updateUser(IUser user)
    {
        return Result.successWithData(userDAO.update((User) user));
    }
    @Override
    public Result<IUser> getUserByMobile(String mobile)
    {
        return Result.successWithData(userDAO.selectByMobile(mobile));
    }

    @Override
    public Result<List<String>> getUserAuthorities(String userId) {
        return Result.successWithData(userDAO.selectAuthorities(userId));
    }

}
