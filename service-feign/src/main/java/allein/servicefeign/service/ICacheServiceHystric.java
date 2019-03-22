package allein.servicefeign.service;

import allein.model.entity.user.User;
import allein.model.exception.CommonException;
import allein.model.output.Result;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class ICacheServiceHystric implements ICacheService {
    @Override
    public Boolean put(String key, Long expire, String value){
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
