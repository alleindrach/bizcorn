package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.service.facade.IStoryService;
import allein.bizcorn.servicefeign.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;

//动态注入proxy
@FeignClient(value = "service",
        configuration = FeignMultipartSupportConfig.class //,
//        fallback = StoryServiceHystric.class
)
public interface StoryServiceProxy extends IStoryService{
}
