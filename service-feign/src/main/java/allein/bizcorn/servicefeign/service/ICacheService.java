package allein.bizcorn.servicefeign.service;

import allein.bizcorn.facade.CacheServiceFacade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = ICacheServiceHystric.class
)
//@RequestMapping(value = "/cache")
public interface ICacheService extends CacheServiceFacade{

}
