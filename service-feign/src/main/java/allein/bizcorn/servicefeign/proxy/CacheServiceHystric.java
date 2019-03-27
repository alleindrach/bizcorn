package allein.bizcorn.servicefeign.proxy;

import org.springframework.stereotype.Component;

@Component
public class CacheServiceHystric implements CacheServiceProxy {

    @Override
    public Boolean put(String key, String value, Long expire) {
        return null;
    }

    @Override
    public String get(String key)
    {
        return null;
    }
    public
    Boolean exists(final String key)
    {
        return false;
    }
    public
    Boolean del(String key)
    {
        return false;
    }
}
