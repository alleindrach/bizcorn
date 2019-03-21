package allein.aspect;

import java.lang.annotation.*;

/**
 * 申明用户授权的注解
 * @author Administrator
 *
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthPermission {
    String[] values();
}
