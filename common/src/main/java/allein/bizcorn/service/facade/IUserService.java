/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade;

import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.Kid;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.gate.IUserServiceGate;
import org.springframework.web.bind.annotation.*;
import java.util.List;


public interface IUserService extends IUserServiceGate {

/*
@Description: 获取缓存里用户登录失败次数，超过一定数量后，需要验证码
@Param:
@Return:
@Author:Alleindrach@gmail.com
@Date:2019/5/10
@Time:2:52 PM
*/
    public Long getUserLoginErrorTimes(String userName);
/*
@Description:递增缓存中用户登录失败次数
@Param:
@Return:
@Author:Alleindrach@gmail.com
@Date:2019/5/10
@Time:2:52 PM
*/
    public Long incUserLoginErrorTimes(String userName);
/*
@Description:缓存中用户登录失败次数清零
@Param:
@Return:
@Author:Alleindrach@gmail.com
@Date:2019/5/10
@Time:2:53 PM
*/
    public Boolean rstUserLoginErrorTimes( String userName);
/*
@Description:获得用户实体，principal可以是id/username/mobile的一种
@Param:
@Return:
@Author:Alleindrach@gmail.com
@Date:2019/5/10
@Time:2:54 PM
*/
    public User getUser(String principal);

    public List<String> getUserAuthorities(String userId);
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
/*
@Description:当成人向小童发消息时，重建绑定关系
@Param:
@Return:
@Author:Alleindrach@gmail.com
@Date:2019/5/10
@Time:3:03 PM
*/
    boolean rebind(User binder, Kid kid);

}
