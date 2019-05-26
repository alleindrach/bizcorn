/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

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
public class CacheAspect implements Ordered,InitializingBean {



	private static final Log LOG = LogFactory.getLog(CacheAspect.class);
	private static final Log accessLog = LogFactory.getLog("ACCESS");

	/***
	 * 检查登录态切面
	 */
	@Pointcut("@annotation(allein.bizcorn.common.annotation.CacheMethod)")
	public void cacheMethod() {
	};

	/***
	 * 执行方法
	 * 
	 * @throws Throwable
	 */
	@Around("cacheMethod()")
	public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			return cacheToRedis(joinPoint);
		} catch (CommonException ce) {
			throw ce;
		}
	}

	private Object cacheToRedis(ProceedingJoinPoint joinPoint) throws Throwable {

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
