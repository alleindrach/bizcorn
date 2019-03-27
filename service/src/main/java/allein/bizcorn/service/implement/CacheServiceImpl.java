package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.service.facade.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CacheServiceImpl implements ICacheService {
    private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

    @Autowired
    private ICacheAccessor cacheAccessor;

    public
    @PutMapping("/cache/{key}/{expire}/{value}")
    Boolean put(@PathVariable String key,@PathVariable String value,@PathVariable Long expire)
    {
        boolean result = false;
        try {
            return cacheAccessor.put(key,value,expire);
        } catch (Exception e) {
           logger.error("Cache Error",e);
        }
        return result;
    }
    public
    @GetMapping("/cache/{key}")
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
    @RequestMapping("/cache/exists/{key}")
    Boolean exists(@PathVariable final String key)
    {
//        return false;
        return cacheAccessor.exists(key);
    }
    public
    @DeleteMapping("/cache/{key}")
    Boolean del(@PathVariable String key)
    {
//        return false;
        return cacheAccessor.del(key);

    }
}
