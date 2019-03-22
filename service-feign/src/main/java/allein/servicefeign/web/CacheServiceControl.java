package allein.servicefeign.web;

import allein.model.output.Result;
import allein.servicefeign.service.ICacheService;
import allein.servicefeign.service.IUserService;
import com.alibaba.fastjson.JSONObject;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

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
    @PutMapping("/{key}/{expire}/{value}")
    Boolean put(@PathVariable  String key,@PathVariable Long expire,@PathVariable  String value)
    {
        Boolean result=cacheService.put(key,expire,value);
        return result;
    }
    public
    @GetMapping("/{key}")
    String get(String key)
    {
        String result=cacheService.get(key);
        return result;
    }

    public
    @RequestMapping("/exists/{key}")
    Boolean exists(final String key)
    {
        return cacheService.exists(key);
    }
    public
    @DeleteMapping("/{key}")
    Boolean del(String key)
    {
        return cacheService.del(key);
    }

}
