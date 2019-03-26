package allein.bizcorn.service.security;


import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.service.facade.IConfigSerivce;
import allein.bizcorn.service.facade.IUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;


@Component
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private IUserService userSerivce;
    @Autowired
    private CustomUserDetailsService detailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IConfigSerivce configService;




    @Override
    protected  org.springframework.security.core.userdetails.UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // 如有其它逻辑处理，可在此处进行逻辑处理...
        return detailsService.loadUserByUsername(username);
    }

    @Override
    protected void additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails  userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String username = userDetails.getUsername();
        User user = userSerivce.getUserByUsername(username);

        // 检查验证码
        if (authentication.getDetails() instanceof CustomWebAuthenticationDetails) {
            if (configService.isEnableCaptcha(userSerivce.getUserLoginErrorTimes(username))) {
                CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
                String inputCaptcha = details.getInputCaptcha();
                String cacheCaptcha = details.getCacheCaptcha();
                if (StringUtils.isEmpty(inputCaptcha) || !StringUtils.equalsIgnoreCase(inputCaptcha, cacheCaptcha)) {
                    throw new AuthenticationServiceException("login.captcha.error");
                }
                authentication.setDetails(null);
            }
        }

        // 检查密码是否正确
        String password = userDetails.getPassword();
        String rawPassword = authentication.getCredentials().toString();
        String md5PasswordAndRandom= DigestUtils.md5DigestAsHex(rawPassword.getBytes())+user.getLoginPasswordRandom();
        boolean match = passwordEncoder.matches(md5PasswordAndRandom, password);
        if (!match) {
            throw new BadCredentialsException("login.username-or-password.error");
        }
    }
}