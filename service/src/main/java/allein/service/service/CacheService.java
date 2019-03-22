package allein.service.service;

import allein.aspect.AuthLogin;
import allein.model.entity.user.User;
import allein.model.output.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cache")
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(UserManageService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    public
    @PutMapping("/{key}/{expire}/{value}")
    Boolean put(@PathVariable String key,@PathVariable Long expire,@PathVariable String value)
    {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
           logger.error("Cache Error",e);
        }
        return result;
    }
    public
    @GetMapping("/{key}")
    String get(@PathVariable String key)
    {
        Object result ;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result=operations.get(key);
            if(result==null)
                return null;
            else
                return (String) result;
        } catch (Exception e) {
            logger.error("Cache Error",e);
            return null;
        }

    }

    public
    @RequestMapping("/exists/{key}")
    Boolean exists(@PathVariable final String key)
    {
        return redisTemplate.hasKey(key);
    }
    public
    @DeleteMapping("/{key}")
    Boolean del(@PathVariable String key)
    {
        if (exists(key)) {
            return redisTemplate.delete(key);
        }
        return false;

    }
}
