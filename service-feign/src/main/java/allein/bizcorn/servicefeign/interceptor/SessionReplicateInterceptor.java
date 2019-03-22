package allein.bizcorn.servicefeign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
public class SessionReplicateInterceptor implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionReplicateInterceptor.class);

    @Autowired
    HttpServletRequest request;

    @Override
    public void apply(RequestTemplate requestTemplate) {

        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return;
            }

            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        requestTemplate.header(name, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("SessionInterceptor exception: ", e);
        }

//        logger.info("{} SessionInterceptor apply begin.",Thread.currentThread().getId());
//        try {
//            String sessionId =  RequestContextHolder.currentRequestAttributes().getSessionId();
//            if (null != sessionId) {
////                requestTemplate.header("Cookie", "SESSION=" + sessionId);
//            }
//            Cookie[] cookies =request.getCookies();
//            if(cookies!=null) {
//                for (int i = 0; i < cookies.length; i++) {
//                    logger.info("Cookie >>>>> {} => {}", cookies[i].getName(), cookies[i].getValue());
//                    requestTemplate.header("Cookie", cookies[i].getName()+"=" + cookies[i].getValue());
//                }
//            }
//        } catch (Exception e) {
//            logger.error("SessionInterceptor exception: ", e);
//        }
    }
}