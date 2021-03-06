package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;

import allein.bizcorn.common.util.ExcelUtil;
import allein.bizcorn.common.util.Masker;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.common.websocket.Status;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.*;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.model.security.Authority;
import allein.bizcorn.model.security.CaptchaResult;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.config.ServiceConfigProp;
import allein.bizcorn.service.db.mongo.dao.BindTokenDAO;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IMessageBrokerService;
import allein.bizcorn.service.facade.IMessageQueueService;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.websocket.WebsocketUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    public User getUserFromSession(){
        String username= SecurityUtil.getUserName();
        if(username!=null)
        {
            return userDAO.select(username);
        }else
        {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_ID_INVALID);
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

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result updateProfile(@RequestBody  JSONObject profile) {
        User user=getUserFromSession();
        if(user==null)
            return  Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_ID_INVALID));

        Profile profilePOJO=JSON.parseObject(JSON.toJSONString(profile),Profile.class);
        user.setProfile(profilePOJO);
        userDAO.save(user);
        return Result.successWithMessage("");
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result<Profile> getProfile() {
        User user=getUserFromSession();
        if(user==null)
            return  Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_ID_INVALID));
        return Result.successWithData(user.getProfile());
    }

    public  Kid getChild(User user)
    {
        if(user.getCurPartner()==null)
        {
            throw new CommonException(ExceptionEnum.BIND_NOT_EXIST);
        }
        User kidUser=user.getCurPartner();
        if(kidUser instanceof LazyLoadingProxy)
            kidUser= (User) ((LazyLoadingProxy) kidUser).getTarget();

        if(kidUser.getRole().getValue()!=Role.KID.getValue())
            throw new CommonException(ExceptionEnum.BIND_KID_INVALID);

        Kid kid=(Kid)kidUser;

        User kidParent=kid.getParent();
//        if(kidParent instanceof LazyLoadingProxy)
//            kidParent=userDAO.get(kidParent.getId());
        if(kidParent==null ||kidParent.getId()!=user.getId())
        {
            throw new CommonException(ExceptionEnum.BIND_RELATION_ERROR);
        }
        return kid;
    }
    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result updateKidProfile(@RequestBody  JSONObject profile) {
        User user=getUserFromSession();

        Profile profilePOJO=JSON.parseObject(JSON.toJSONString(profile),Profile.class);
        Kid kid=getChild(user);
        kid.setProfile(profilePOJO);
        userDAO.save(kid);
        return Result.successWithMessage("");
    }
    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result<Profile> getKidProfile() {
        User user=getUserFromSession();
        Kid kid=getChild(user);
        return Result.successWithData(kid.getProfile());
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getSelfInfo() {
        User user=getUserFromSession();
        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.SimpleSerializer());
        config.put(Kid.class,new User.SimpleSerializer());
        JSONObject result=JSON.parseObject(JSON.toJSONString(user,config));
        return Result.successWithData(result);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getFriends() {
        User  user=getUserFromSession();

        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.SimpleSerializer());
        config.put(Kid.class,new User.SimpleSerializer());
        JSONArray result=JSONArray.parseArray( JSON.toJSONString(user.getFriends(),config));
        return Result.successWithData(result);
    }


    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result bindFriend(@PathVariable("id") String id) {
        User  user=getUserFromSession();
        if(!user.hasFriend(id))
        {
           return  Result.failWithException(new CommonException(ExceptionEnum.BIND_FRIEND_INVALID));
        }
        User bindee= userDAO.get(id);
        if(bindee==null)
            return  Result.failWithException(new CommonException(ExceptionEnum.BIND_FRIEND_NOT_EXIST));


        BindToken newBindToken=new BindToken(bindee,user);
        newBindToken = bindTokenDAO.save(newBindToken);
        Message bindRequireMsg = Message.BindRequireMessage(newBindToken);
        messageBrokerService.send(bindRequireMsg);
        return Result.successWithData(newBindToken.getId());
    }

    public
    @PreAuthorize("hasAnyRole('USER','user')")
//    @Transactional
//    @AuthLogin(injectUidFiled = "userId")
    Result update(
            @RequestParam(value = "mobile") String mobile
    ) {
        logger.info("session_id>>>>>{}",RequestContextHolder.getRequestAttributes().getSessionId());
        String username=SecurityUtil.getUserName();
        if(username!=null){
                userDAO.update(new Query(Criteria.where("username").is(username)),new Update().set("mobile",mobile));
                return Result.successWithData(this.getUser(username));
        }

        return  Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_ID_INVALID));
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
    public Long getUserLoginErrorTimes(String userName)
    {
        return cacheAccessor.getLong(this.ErrorTimesKey+userName);
    }
    @Override
    public Long incUserLoginErrorTimes(String userName)
    {
        return cacheAccessor.inc(this.ErrorTimesKey+userName);
    }
    @Override
    public Boolean rstUserLoginErrorTimes(String userName)
    {
        return cacheAccessor.del(this.ErrorTimesKey+userName);
    }


    @Override
    public User getUser(String principal) {
        return userDAO.select(principal);
    }

    @Override
    public List<String> getUserAuthorities(@PathVariable("id") String userId) {
        User user=userDAO.get(userId);
        List<String> auths=new ArrayList<>();
        for (String auth:user.getAuthorities()
             ) {
            auths.add(auth);
        }
        return auths;
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
        if(username==null || username.isEmpty()){
            username="U"+mobile;
        }
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

        ArrayList<String> auths=new ArrayList<String>();
        auths.add(Authority.ROLE_USER.getValue());
        newuser.setAuthorities(auths);
        userDAO.save(newuser);
        return  Result.successWithData(newuser);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result register(@PathVariable("mac")  String mac) {

        if(userDAO.selectByName(mac)!=null){
            return Result.failWithException(new CommonException(ExceptionEnum.USER_EXISTS));
        }

        Kid kid=new Kid();
        kid.setUsername(mac);
        String mobileCaptchaKey=null;

        kid.setPassword( DigestUtils.md5DigestAsHex(mac.toString().getBytes()));
        ArrayList<String> auths=new ArrayList<String>();
        auths.add(Authority.ROLE_USER.getValue());
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
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result newBindToken() {
        Kid kid = (Kid) getUserFromSession();
        if (kid == null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST));
        }
        BindToken newBindToken=new BindToken(kid);
        bindTokenDAO.save(newBindToken);
        return Result.successWithData(newBindToken);
    }


    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result confirmBind(@PathVariable(value = "token") String tokenId) {
        Kid kid = (Kid) getUserFromSession();
        if (kid == null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_KID_ACCOUNT_NOT_EXIST));
        }
        BindToken token= bindTokenDAO.get(tokenId);
        if(token.getStatus()!=BindTokenStatus.FIRED)
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
                    binder.addFriend(kid);
                    kid.addFriend(binder);
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
                    binder.addFriend(kid);
                    kid.addFriend(binder);
                    isDirt=true;
                }else
                {
                    if(kid.isValidElder(binder.getMobile()))
                    {
                        kid.setCurPartner(binder);
                        binder.setCurPartner(kid);
                        binder.addFriend(kid);
                        kid.addFriend(binder);
                        isDirt=true;
                    }
                    else if(kid.getParent()!=null && kid.getParent().getId().compareToIgnoreCase(binder.getId())==0)
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
        if(token.getStatus()!=BindTokenStatus.FIRED)
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
        return Result.successWithData(token);
    }

    @Override
    public Result setElderNumbers(@RequestBody JSONObject params)
    {
        User user=getUserFromSession();
        Kid kid=getChild(user);
        List<String> eldersMobile=params.getJSONArray("elders").toJavaList(String.class);
        kid.setElderNumbers(eldersMobile);
        userDAO.save(kid);
        JSONObject jsonObject=new JSONObject();
        List numbers=kid.getElderNumbers();
        if(numbers==null)
            numbers=new ArrayList();
        jsonObject.put("elders",new JSONArray(numbers));
        return Result.successWithData(jsonObject);
    }

    @Override
    public Result getElderNumbers() {
        User user=getUserFromSession();
        Kid kid=getChild(user);
        JSONObject jsonObject=new JSONObject();
        List numbers=kid.getElderNumbers();
        if(numbers==null)
            numbers=new ArrayList();
        jsonObject.put("elders",new JSONArray(numbers));
        return Result.successWithData(jsonObject);

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result firebind(
            @PathVariable(value = "token") String tokenId
    ) {
        BindToken token =bindTokenDAO.get(tokenId);
        if(token==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_NOT_EXIST));
        }
        if(token.getCreateDate().before(new Date(System.currentTimeMillis()-serviceProp.getBindTokenTimout()*1000))) {
            token.setStatus(BindTokenStatus.EXPIRED);
            bindTokenDAO.save(token);
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_TOKEN_EXPIRED));
        }
        Kid kid = (Kid) token.getBindee();
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
        token.setBinder(binder);
        token.setStatus(BindTokenStatus.FIRED);
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

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result changePassowrd(
            @RequestParam(value = "password") String password,
                                 @RequestParam(value = "oldPassword") String oldPassword
    ) {
        User user =  getUserFromSession();
        if (user == null) {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }
        String md5Pwd=DigestUtils.md5DigestAsHex(oldPassword.toString().getBytes());
        if(!user.getPassword().equals(md5Pwd))
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_PASSWORD_ERROR));
        }
        else
        {
            user.setPassword(DigestUtils.md5DigestAsHex(password.toString().getBytes()));
            userDAO.save(user);
            return Result.successWithMessage("修改成功");
        }

    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    /*
    @Description:
    @Param:[params]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/5/21
    @Time:12:11 PM
    */
    public Result adminUserList( @RequestBody JSONObject params) {

        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.FullSerializer());
        config.put(Kid.class,new User.FullSerializer());

        JSONObject result= (JSONObject)userDAO.list(params);

        result.put("list",JSONArray.parse( JSON.toJSONString(result.get("list"),config)));
        return Result.successWithData(result);
    }

    @PreAuthorize("hasAnyRole('USER','user')")
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

    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @ResponseBody
    public Result adminChangeUserAuthorities(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "authorities") String authorities)
    {
        User user=userDAO.get(id);
        if(user==null )
        {
            return Result.failWithException(new CommonException(ExceptionEnum.ADMIN_USER_ACCOUNT_NOT_EXIST));
        }
        JSONArray jsonAuthorities =null;
        try {
            jsonAuthorities = JSONArray.parseArray(authorities);
        }catch(Exception ex)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.ADMIN_USER_AUTORITIES_INVALID));
        }
        user.setAuthorities(jsonAuthorities.toJavaList(String.class));
        userDAO.save(user);
        return Result.successWithData(user.getAuthorities());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminUpdateUser(@RequestBody JSONObject user) {
//        User user=JSON.parseObject(userJson,User.class);
        User userInDB=userDAO.get(user.getString("id"));
        if(userInDB==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.ADMIN_USER_ACCOUNT_NOT_EXIST));
        }
        userInDB.setAuthorities(user.getJSONArray("authorities").toJavaList(String.class));
        userInDB.setEnabled(user.getInteger("enabled"));
        userDAO.save(userInDB);
        return Result.successWithData(userInDB.fullJsonString());
    }
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @RequestMapping(value = "/admin/user/add",consumes={ "application/json", "text/plain" },produces = {"application/json"})
    public Result adminAddUser(
            @RequestBody JSONObject jsoUser)
    {
        User userInDB=new User();

        userInDB.setAuthorities(jsoUser.getJSONArray("authorities").toJavaList(String.class));
        userInDB.setEnabled(jsoUser.getInteger("enabled"));
        userInDB.setUsername(jsoUser.getString("username"));
        userInDB.setMobile(jsoUser.getString("mobile"));
        if(jsoUser.getString("role")!=null)
            userInDB.setRole(Role.valueOf(jsoUser.getString("role")));
        userDAO.save(userInDB);
        return Result.successWithData(userInDB.fullJsonString());

    }
    /*
    @Description:
    @Param:
    格式：
    第2列为mac地址，只保留字符
    @Return:
    @Author:Alleindrach@gmail.com
    @Date:2019/5/24
    @Time:11:05 AM
    */
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @RequestMapping(value = "/admin/user/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = {"application/json"})
    public Result adminImportUser(
            @RequestPart MultipartFile file) throws Exception {


        InputStream ins = file.getInputStream();
        Map<String,List<List<Object>>>  data= ExcelUtil.getBankListByExcel(ins,file.getOriginalFilename());
        List<List<Object>> sheet= (List<List<Object>>) data.values().toArray()[0];
        List<Result> results=new ArrayList<>(100);
        for(List<Object> row:sheet){
            String mac = (String) row.get(1);
            try {
                if(mac!=null && !mac.isEmpty())
                {
                    Result result = this.register(mac);
                    if(result.isSuccess())
                    {
                        JSONObject jso=new JSONObject();
                        jso.put("name",mac);
                        jso.put("id",((JSONObject)result.getData()).getString("id"));
                        results.add(Result.successWithData(jso));
                    }
                    else {
                        JSONObject jso=new JSONObject();
                        jso.put("name",mac);
                        jso.put("message",result.getMessage());
                        results.add(Result.failWithMessage(result.getMessage(),jso));
                    }
                }

            }catch(Exception ex)
            {
                JSONObject jso=new JSONObject();
                jso.put("name",mac);
                jso.put("message",ex.getMessage());
                results.add(Result.failWithMessage(ex.getMessage(),jso));
            }
        }
        return Result.successWithData(results);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminCloseWS(@PathVariable("username")  String username) {
        WebsocketUtil.removeSession(username,true);
        return Result.successWithMessage("");
    }


}
