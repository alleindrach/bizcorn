package allein.bizcorn.service.security.config;

import allein.bizcorn.service.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
@EnableWebMvcSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)//开启security注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private SecurityProperties properties;
    @Autowired
    private CustomAuthenticationDetailsSource authenticationDetailsSource;
    @Autowired
    private CustomAuthenticationProvider authenticationProvider;
    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomAuthenticationLogoutHandler authenticationLogoutHandler;

    @Autowired
    private CustomAuthenticationLogoutSuccessHandler authenticationLogoutSuccessHandler;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
//        WebSecurity主要是配置跟web资源相关的，比如css、js、images等等，但是这个还不是本质的区别，关键的区别如下：
//        ingore是完全绕过了spring security的所有filter，相当于不走spring security
//        https://www.baeldung.com/security-none-filters-none-access-permitAll
        web.ignoring().antMatchers("/websocket.html","/common/captcha.jpg","/user/mobile/captcha","/register","/password/reset");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//      permitAll没有绕过spring security，其中包含了登录的以及匿名的，此处禁止了匿名，所以即使permitall也会被deny
//        AnonymousAuthenticationFilter 的 主要功能就是给没有登陆的用户，填充AnonymousAuthenticationToken到SecurityContextHolder的Authentication，后续依赖Authentication的代码可以统一处理。
//        参见 SecurityFilters.class,FilterComparator
        http.authorizeRequests()
                .antMatchers("/user","/user/*","/kid/register/*","/kid/*","/websocket","/story/*","/sound/*","/password/change","/admin/*").authenticated()
                .and()
                .formLogin().loginPage("/login")
                .authenticationDetailsSource(authenticationDetailsSource)
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .logout().logoutUrl("/logout").addLogoutHandler(authenticationLogoutHandler).logoutSuccessHandler(authenticationLogoutSuccessHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new UnauthenticatedEntryPoint()) //重新授权入口，比如用户未登录
                .accessDeniedHandler(new AuthorizationFailure())//禁止访问入口，比如用户无权
                .and()
                .csrf().disable()
                .anonymous().disable()
                .sessionManagement()
                .invalidSessionStrategy(new CustomInvalidSessionStrategy())//session过期
                //最大登录数，不限制
                .maximumSessions(-1)
                //达到最大用户数时，前面被挤掉的用户处理
                .expiredSessionStrategy(new CustomExpiredSessionStrategy())//配置并发登录，-1表示不限制
                .sessionRegistry(sessionRegistry())
                ;

//                .antMatchers("/", "/home").permitAll()
//                //其他地址的访问均需验证权限
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                //指定登录页是"/login"
//                .loginPage("/login")
//                .defaultSuccessUrl("/hello")//登录成功后默认跳转到"/hello"
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/home")//退出登录后的默认url是"/home"
//                .permitAll();

    }

    /**
     * 设置认证处理器
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
        super.configure(auth);
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(customUserDetailsService())
//                .passwordEncoder(passwordEncoder());
//    }

    /**
     * 设置用户密码的加密方式为MD5加密
     * @return
     */
    @Bean
    public MD5PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();

    }
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    /**
     * 自定义UserDetailsService，从数据库中读取用户信息
     * @return
     */
//    @Bean
//    public CustomUserDetailsService customUserDetailsService(){
//        return new CustomUserDetailsService();
//    }

}