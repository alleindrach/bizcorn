package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.service.facade.gate.IUserServiceGate;
import org.springframework.cloud.openfeign.FeignClient;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = UserServiceHystric.class
)
public interface UserServiceProxy extends IUserServiceGate {
}
