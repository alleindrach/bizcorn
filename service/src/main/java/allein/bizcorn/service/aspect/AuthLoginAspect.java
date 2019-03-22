package allein.bizcorn.service.aspect;



import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 检查用户登录态切面
 * 
 * @author llh
 * 
 */
@Aspect
@Component
public class AuthLoginAspect implements Ordered,InitializingBean {



	private static final Log LOG = LogFactory.getLog(AuthLoginAspect.class);
	private static final Log accessLog = LogFactory.getLog("ACCESS");

	/***
	 * 检查登录态切面
	 */
	@Pointcut("@annotation(allein.bizcorn.common.aspect.AuthLogin)")
	public void allAuthLoginMethod() {
	};

	@Value("${bizcorn.session.attribute.timeout}")
	String sessionAttrTimeout;
	@Value("${bizcorn.session.attribute.user}")
	String sessionAttrUser;
	/***
	 * 执行方法
	 * 
	 * @throws Throwable
	 */
	@Around("allAuthLoginMethod()")
	public Object authLogin(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			return authLoginInternal(joinPoint);
		} catch (CommonException ce) {
			throw ce;
		}
	}

	private Object authLoginInternal(ProceedingJoinPoint joinPoint) throws Throwable {


		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		HttpServletRequest request = sra.getRequest();
		Object userSession=request.getSession().getAttribute(sessionAttrUser);
		Object timeout=request.getSession().getAttribute(sessionAttrTimeout);
		if(userSession==null||timeout==null || System.currentTimeMillis()/1000L>Long.parseLong(timeout.toString()))
			throw new CommonException(ExceptionEnum.USER_NOT_LOGIN);
		Object[] args=joinPoint.getArgs();
		Object x=joinPoint.getTarget();

		return joinPoint.proceed();

	}

	public int getOrder() {
		return 1;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
//		this.webUtils=new WebUtils(redisService);
	}
}
