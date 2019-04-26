package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.service.facade.IStoryService;
import org.springframework.cloud.openfeign.FeignClient;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = StoryServiceHystric.class
)
public interface StoryServiceProxy extends IStoryService{
}
