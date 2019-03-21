package allein.servicefeign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class SessionInterceptor implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    @Autowired
    HttpServletRequest request;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        logger.info("{} SessionInterceptor apply begin.",Thread.currentThread().getId());
        try {
            String sessionId =  RequestContextHolder.currentRequestAttributes().getSessionId();
            if (null != sessionId) {
//                requestTemplate.header("Cookie", "SESSION=" + sessionId);
            }
            Cookie[] cookies =request.getCookies();
            if(cookies!=null) {
                for (int i = 0; i < cookies.length; i++) {
                    logger.info("Cookie >>>>> {} => {}", cookies[i].getName(), cookies[i].getValue());
                    requestTemplate.header("Cookie", cookies[i].getName()+"=" + cookies[i].getValue());
                }
            }
        } catch (Exception e) {
            logger.error("SessionInterceptor exception: ", e);
        }
    }
}