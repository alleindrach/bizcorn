package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;

import allein.bizcorn.common.util.Masker;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.common.websocket.Status;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.*;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.model.security.CaptchaResult;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.config.ServiceConfigProp;
import allein.bizcorn.service.db.mongo.dao.BindTokenDAO;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IMessageBrokerService;
import allein.bizcorn.service.facade.IMessageQueueService;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@RestController
@RefreshScope
//@RequestMapping("/mongo")
public class UserServiceMongoImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceMongoImpl.class);
//
//    @Value("${bizcorn.session.prefix}")
//    String sessionPrefix;
//
//    @Value("${bizcorn.session.attribute.user}")
//    String sessionAttrUser;
//
//    @Value("${bizcorn.session.attribute.timeout}")
//    String sessionAttrTimeout;
//
//    @Value("${bizcorn.session.timeout}")
//    Long sessionTimeout;
//

    @Autowired
    ICacheAccessor cacheAccessor;
    @Autowired
    ServiceConfigProp serviceProp;
    @Autowired
    UserDAO userDAO;
    @Autowired
    BindTokenDAO bindTokenDAO;
    @Autowired
    private CaptchaImageHelper captchaImageHelper;

    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;
    @Autowired
    private IMessageBrokerService messageBrokerService;
    @Autowired
    private IMessageQueueService messageQueueService;

    private User getUserFromSession(){
        String username= SecurityUtil.getUserName();
        if(username!=null)
        {
            return userDAO.select(username);
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
    public User  getUserByMobile(@PathVariable("mobile") String mobile)
    {
        User user=userDAO.findOne(Query.query(Criteria.where("mobile").is(mobile)));
        return user;
    }

    @Override
    public User getUser(String principal) {
        return userDAO.select(principal);
    }

    @Override
    public Result<List<String>> getUserAuthorities(@PathVariable("id") String userId) {
        User user=userDAO.get(userId);
        List<String> auths=new ArrayList<>();
        for (String auth:user.getAuthorities()
             ) {
            auths.add(auth);
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

        if(userDAO.select(username)!=null||userDAO.select(mobile)!=null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_EXISTS));
        }
        if(username.matches("^[0-9]*$"))
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NAME_INVALID));
        }
        if(!mobile.matches("^[0-9]*$"))
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_MOBILE_INVALID));
        }

        User newuser=new User();
        newuser.setUsername(username);

        CaptchaResult captchaResult=captchaMessageHelper.checkCaptcha(mobileCaptchaKey,captcha,mobile,SecurityConstants.SECURITY_KEY,true);
        if(!captchaResult.isSuccess())
            Result.failWithException(new CommonException(ExceptionEnum.CAPTCH_INVALID));

        newuser.setPassword( DigestUtils.md5DigestAsHex(password.toString().getBytes()));
        newuser.setMobile(mobile);

        HashSet<String> auths=new HashSet<String>();
        auths.add("ROLE_USER");
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
        HashSet<String> auths=new HashSet<String>();
        auths.add("ROLE_USER");
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
    public Result confirmBind(@PathVariable(value = "token") String tokenId) {
        Kid kid = (Kid) getUserFromSession();
        if (kid == null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_KID_ACCOUNT_NOT_EXIST));
        }
        BindToken token= bindTokenDAO.get(tokenId);
        if(token.getStatus()!=BindTokenStatus.INIT)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_INVALID));
        }
        if(token==null) {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_NOT_EXIST));
        }
        if(token.getBindee().getId().compareToIgnoreCase(kid.getId())!=0) {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_INVALID));
        }
        if(token.getCreateDate().before(new Date(System.currentTimeMillis()-serviceProp.getBindTokenTimout()*1000))) {
            token.setStatus(BindTokenStatus.EXPIRED);
            bindTokenDAO.save(token);
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_EXPIRED));
        }
        User binder=token.getBinder();
        if(binder==null) {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_INVALID));
        }

        boolean isDirt=false;
        switch (binder.getRole())
        {
            case KID:
                if(kid.isCanBind()){
                    binder.setCurPartner(kid);
                    kid.setCurPartner(binder);
                    isDirt=true;
                }else
                {
                    return Result.failWithException(new CommonException(ExceptionEnum.BIND_KID_STATUS_INVALID));
                }
                break;
            default:
                if(kid.getParent()==null)
                {
                    kid.setParent(binder);
                    kid.setCurPartner(binder);
                    binder.setCurPartner(kid);
                    isDirt=true;
                }else
                {
                    if(kid.isValidElder(binder.getMobile()))
                    {
                        kid.setCurPartner(binder);
                        binder.setCurPartner(kid);
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
                token.setStatus(BindTokenStatus.CONFIRMED);
                bindTokenDAO.save(token);
//                bindTokenDAO.deleteById(token);
                userDAO.save(binder);
                userDAO.save(kid);

            }
            catch(Exception ex){
                logger.error("绑定错误",ex);
                return  Result.failWithException(new CommonException(ExceptionEnum.BIND_SAVE_ERROR));
            }
        }
        Message msgAck=Message.BindAckMessage(token,Result.successWithMessage("绑定成功"));
        messageBrokerService.send(msgAck);
        return Result.successWithMessage("绑定成功");
    }

    @Override
    public Result queryBind(@PathVariable(value = "token") String tokenId) {
        User bindParticipator =  getUserFromSession();
        if (bindParticipator == null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }
        BindToken token= bindTokenDAO.get(tokenId);
        if(token.getStatus()!=BindTokenStatus.INIT)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_INVALID));
        }
        if(token==null) {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_NOT_EXIST));
        }
        if(token.getBindee().getId().compareToIgnoreCase(bindParticipator.getId())!=0
                && token.getBinder().getId().compareToIgnoreCase(bindParticipator.getId())!=0) {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_INVALID));
        }
        return Result.successWithData(token.toResultJson());
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result firebind(
            @PathVariable(value = "mac") String mac
    ) {
        Kid kid = (Kid) userDAO.selectByName(mac);
        if (kid == null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_KID_ACCOUNT_NOT_EXIST));
        }
        if (kid.getRole() != Role.KID) {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_KID_INVALID));
        }
        User binder = getUserFromSession();
        if (binder == null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST));
        }
        BindToken token = new BindToken(binder,kid);
        token = bindTokenDAO.save(token);
        Message bindRequireMsg = Message.BindRequireMessage(token);
        messageBrokerService.send(bindRequireMsg);
        return Result.successWithData(token.getId());


    }

    @Override
    public void checkIn(String username) {
        User user=userDAO.selectByName(username);
        if(user!=null)
        {
            user.setStatus(Status.ON_LINE);
            user.setLastVisit(new Date());
            userDAO.save(user);
        }
    }

    @Override
    public void checkOut(String username) {
        User user=userDAO.selectByName(username);
        if(user!=null)
        {
            user.setStatus(Status.OFF_LINE);
            userDAO.save(user);
        }
    }

    @Override
    public boolean rebind(User binder, Kid kid) {
        if(kid.getRole()!=Role.KID)
        {
            throw new CommonException(ExceptionEnum.BIND_KID_INVALID);
        }

        if(kid.getCurPartner()==null ||kid.getCurPartner().getId().compareToIgnoreCase(binder.getId())!=0 )
        {
            if(binder.getRole()==Role.ADULT){
                //如果当前用户的绑定关系的当前绑定对象不是当前用户，纠正
                if(kid.isValidElder(binder.getMobile()))
                {
                    kid.setCurPartner(binder);
                    userDAO.save(kid);
                    return true;
                }
                else
                {
                    throw new CommonException(ExceptionEnum.BIND_USER_INVALID);
                }
            }else
            {
                throw new CommonException(ExceptionEnum.BIND_USER_INVALID);
            }

        }
        return false;
    }

    @Override
    public Result resetPassowrd(String password, String captcha, String mobile, String mobileCaptchaKey) {
        User user=userDAO.select(mobile);
        if(user==null){
            return Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST));
        }

        CaptchaResult captchaResult=captchaMessageHelper.checkCaptcha(mobileCaptchaKey,captcha,mobile,SecurityConstants.SECURITY_KEY,true);
        if(!captchaResult.isSuccess())
            Result.failWithException(new CommonException(ExceptionEnum.CAPTCH_INVALID));


        try {
            user.setPassword( DigestUtils.md5DigestAsHex(password.toString().getBytes()));
            userDAO.save(user);
        }catch(DuplicateKeyException exception)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_PASSWORD_RESET_FAIL));
        }
        return  Result.successWithData(getMaskedUser(user));
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
