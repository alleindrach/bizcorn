package allein.servicerouterzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


@Component
public class SessionFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String sessionId = request.getSession().getId();
//        ctx.addZuulRequestHeader("Cookie", "SESSION=" + sessionId);
//        Cookie[] cookies =request.getCookies();
        logger.info(" {} >>> {}",  request.getRequestURL().toString(),sessionId);
        Enumeration e = request.getSession().getAttributeNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            logger.info("Session >>>>> {} => {}",name,request.getSession().getAttribute(name));
        }
        Cookie[] cookies =request.getCookies();
        if(cookies!=null) {
            for (int i = 0; i < cookies.length; i++) {
                logger.info("Cookie >>>>> {} => {}", cookies[i].getName(), cookies[i].getValue());
            }
        }
//        ctx.setSendZuulResponse(true);// 对该请求进行路由
//        ctx.setResponseStatusCode(200); // 返回200正确响应
        return null;
    }
}