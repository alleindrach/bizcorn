package allein.bizcorn.service.security;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.service.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;

// 自定义验证方法
@Component
public class SimpleAuthenticationManager implements AuthenticationManager {
    static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();
    @Autowired
    private UserDAO userDAO;

    // 构建一个角色列表
    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 验证方法
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        // 这里我们自定义了验证通过条件：username与password相同就可以通过认证

        User user = userDAO.selectByNameCached(auth.getName());

        if (user == null) {
            throw new BadCredentialsException("用户账户密码错误！");
        }
        MD5PasswordEncoder encoder=new MD5PasswordEncoder();

        String md5password=encoder.encode(auth.getCredentials().toString())+user.getLoginPasswordRandom();

        if(encoder.matches(md5password,user.getLoginPassword()))
        {
            ArrayList<SimpleGrantedAuthority> authorities= new ArrayList<SimpleGrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(auth.getName(), md5password,authorities);
        }

        // 没有通过认证则抛出密码错误异常
        throw new BadCredentialsException("用户账户密码错误！");
    }
}
