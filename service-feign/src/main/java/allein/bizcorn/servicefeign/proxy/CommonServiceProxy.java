package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.service.facade.gate.ICommonServiceGate;
import org.springframework.cloud.openfeign.FeignClient;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = CommonServiceHystric.class
)
public interface CommonServiceProxy extends ICommonServiceGate {

}
