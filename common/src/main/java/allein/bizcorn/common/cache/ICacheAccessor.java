package allein.bizcorn.common.cache;

import java.util.concurrent.TimeUnit;

public interface ICacheAccessor  {
    Boolean put(String key,String value,Long expire);
    Boolean put(String key,String value,Long expire,TimeUnit tu);
    Long inc(String key);
    Long inc(String key,Long step);
    Boolean expire(String key,Long expire);
    Boolean expire(String key,Long expire, TimeUnit tu);
    String get(String key);
    Long getLong(String key);
    Boolean exists(final String key);
    Boolean del(String key);
}