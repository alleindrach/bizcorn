package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.service.facade.ICacheService;
import org.springframework.cloud.openfeign.FeignClient;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = CacheServiceHystric.class
)
//@RequestMapping(value = "/cache")
public interface CacheServiceProxy extends ICacheService{

}
