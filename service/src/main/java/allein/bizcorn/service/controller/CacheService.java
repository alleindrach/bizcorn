package allein.bizcorn.service.controller;

import allein.bizcorn.common.cache.ICacheAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ICacheAccessor cacheAccessor;

    public
    @PutMapping("/{key}/{expire}/{value}")
    Boolean put(@PathVariable String key,@PathVariable Long expire,@PathVariable String value)
    {
        boolean result = false;
        try {
            return cacheAccessor.put(key,expire,value);
        } catch (Exception e) {
           logger.error("Cache Error",e);
        }
        return result;
    }
    public
    @GetMapping("/{key}")
    String get(@PathVariable String key)
    {
//        return null;
        try {
            return cacheAccessor.get(key);
        } catch (Exception e) {
            logger.error("Cache Error",e);
            return null;
        }
    }

    public
    @RequestMapping("/exists/{key}")
    Boolean exists(@PathVariable final String key)
    {
//        return false;
        return cacheAccessor.exists(key);
    }
    public
    @DeleteMapping("/{key}")
    Boolean del(@PathVariable String key)
    {
//        return false;
        return cacheAccessor.del(key);

    }
}
