package allein.bizcorn.common.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    public static String getUserName(){
//        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() !=null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails)
//        {
//            String userPrincipal=((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
//            return userPrincipal;
//        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(usernamePasswordAuthenticationToken !=null &&
                usernamePasswordAuthenticationToken.isAuthenticated() &&
                usernamePasswordAuthenticationToken.getPrincipal() instanceof UserDetails &&
                ((UserDetails) usernamePasswordAuthenticationToken.getPrincipal()).getUsername()!=null) {
            String username = ((UserDetails) usernamePasswordAuthenticationToken.getPrincipal()).getUsername();
            return username;
        }
        return null;
    }
}
