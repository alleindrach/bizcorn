package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.service.facade.ICommonService;
import allein.bizcorn.service.facade.IUserService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = CommonServiceHystric.class
)
public interface CommonServiceProxy extends ICommonService{

}
