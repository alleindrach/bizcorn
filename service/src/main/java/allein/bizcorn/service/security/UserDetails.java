package allein.bizcorn.service.security;


import allein.bizcorn.model.facade.IUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetails extends  org.springframework.security.core.userdetails.User {
    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public UserDetails(IUser user, ArrayList<SimpleGrantedAuthority> authorities )
    {

        super(user.getUsername(),
                user.getPassword(),
                authorities);

    }
}
