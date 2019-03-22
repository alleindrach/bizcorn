package allein.bizcorn.servicefeign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//动态注入proxy
@FeignClient(value = "service"
        , fallback = ICacheServiceHystric.class
)
//@RequestMapping(value = "/cache")
public interface ICacheService {
    public
    @PutMapping("/cache/{key}/{expire}/{value}")
    Boolean put(@PathVariable  String key, @PathVariable Long expire,@PathVariable  String value);
    public
    @GetMapping("/cache/{key}")
    String get(@PathVariable String key);
    public
    @RequestMapping("/cache/exists/{key}")
    Boolean exists(@PathVariable final String key);
    public
    @DeleteMapping("/cache/{key}")
    Boolean del(@PathVariable String key);
}
