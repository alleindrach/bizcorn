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
    Long inc(String key)
    {
        return this.inc(key,1L);
    }
    public
    Long inc(String key,Long step)
    {
        Long result=null;

        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result=operations.increment(key, step);
        } catch (Exception e) {
            logger.error("Cache Error",e);
        }
        return result;

    }
    public
    Boolean expire(String key,Long expire)
    {
        return this.expire(key,expire,TimeUnit.SECONDS);
    }
    public
    Boolean expire(String key,Long expire, TimeUnit tu)
    {
        boolean result = false;
        try {
            redisTemplate.expire(key, expire,tu);
            result = true;
        } catch (Exception e) {
            logger.error("Cache Error",e);
        }
        return result;
    }
    public
    Boolean put(String key,String value,Long expire)
    {
        return this.put(key,value,expire,TimeUnit.SECONDS);
    }
    public
    Boolean put(String key,String value,Long expire,TimeUnit tu)
    {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expire,tu);
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
    Long getLong(String key)
    {
        String result=this.get(key);
        if(result!=null)
            return Long.parseLong(result);
        return 0L;
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
