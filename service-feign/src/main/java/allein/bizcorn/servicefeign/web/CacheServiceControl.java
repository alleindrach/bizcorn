package allein.bizcorn.servicefeign.web;

import allein.bizcorn.servicefeign.service.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RefreshScope
@RequestMapping("/cache")
public class CacheServiceControl {

    private static final Logger logger = LoggerFactory.getLogger(CacheServiceControl.class);


    @Autowired
    ICacheService cacheService;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    public
    @PutMapping("/{key}/{value}/{expire}")
    Boolean put(@PathVariable  String key,@PathVariable Long expire,@PathVariable  String value)
    {
        Boolean result=cacheService.put(key,expire,value);
        return result;
    }
    public
    @GetMapping("/{key}")
    String get(@PathVariable String key)
    {
        String result=cacheService.get(key);
        return result;
    }

    public
    @RequestMapping("/exists/{key}")
    Boolean exists(@PathVariable final String key)
    {
        return cacheService.exists(key);
    }
    public
    @DeleteMapping("/{key}")
    Boolean del(@PathVariable String key)
    {
        return cacheService.del(key);
    }

}
