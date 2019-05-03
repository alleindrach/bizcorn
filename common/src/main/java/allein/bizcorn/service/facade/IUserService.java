/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade;

import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.gate.IUserServiceGate;
import org.springframework.web.bind.annotation.*;
import java.util.List;


public interface IUserService extends IUserServiceGate {


    @RequestMapping(value = "/user/byname/{username}",method = RequestMethod.GET)
    public Result<IUser> getUserByUsername(@PathVariable("username") String userName);

    @RequestMapping(value = "/user/masked/{username}",method = RequestMethod.GET)
    public Result<IUser> getMaskedUserByUsername(@PathVariable("username") String userName);


    @RequestMapping(value = "/user/login/errortimes/{username}",method = RequestMethod.GET)
    public Result<Long> getUserLoginErrorTimes(@PathVariable("username") String userName);

    @RequestMapping("/user/login/errortimes/inc/{username}")
    public Result<Long> incUserLoginErrorTimes(@PathVariable("username") String userName);

    @RequestMapping("/user/login/errortimes/rst/{username}")
    public Result<Boolean> rstUserLoginErrorTimes(@PathVariable("username") String userName);

    public User getUserByMobile(String mobile);
    public User getUser(String principal);

    @RequestMapping("/user/authorities/id")
    public Result<List<String>> getUserAuthorities(@PathVariable("id") String userId);
/*
    @Description: 上线处理
    @Param:
    @Return:
    @Author:Alleindrach@gmail.com
    @Date:2019/5/1
    @Time:6:04 PM
    */
    void checkIn(String username);
    /*
    @Description:下线处理
    @Param:
    @Return:
    @Author:Alleindrach@gmail.com
    @Date:2019/5/1
    @Time:6:04 PM
    */
    void checkOut(String username);


}
