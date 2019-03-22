package allein.bizcorn.common.cache;

public interface ICacheAccessor  {
        Boolean put(String key,Long expire,String value);
        String get(String key);
        Boolean exists(final String key);
        Boolean del(String key);
    }