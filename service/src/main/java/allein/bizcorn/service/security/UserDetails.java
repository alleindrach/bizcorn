package allein.bizcorn.service.security;

import allein.bizcorn.common.model.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.DigestUtils;

import java.util.Collection;

public class UserDetails extends  org.springframework.security.core.userdetails.User {
    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public UserDetails(User user)
    {
        super(user.getName(), DigestUtils.md5DigestAsHex(user.getLoginPassword().getBytes())+user.getLoginPasswordRandom(),null);
    }
}
