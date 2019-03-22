package allein.bizcorn.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Component
public class CacheAccessor implements ICacheAccessor {
    private static final Logger logger = LoggerFactory.getLogger(CacheAccessor.class);

    @Autowired
    private RedisTemplate redisTemplate;

    public
    Boolean put(String key,Long expire,String value)
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
    String get(String key)
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
    Boolean exists(final String key)
    {
        return redisTemplate.hasKey(key);
    }
    public
    Boolean del(String key)
    {
        if (exists(key)) {
            return redisTemplate.delete(key);
        }
        return false;

    }
}
