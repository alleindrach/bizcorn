package allein.bizcorn.common.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 申明用户认证登录的注解
 * @author Administrator
 *
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthLogin {
    @AliasFor("value")
    String injectUidFiled() default "";
}
