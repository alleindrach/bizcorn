package allein.bizcorn.common.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    public static String getUserName(){
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() !=null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails)
        {
            String userPrincipal=((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            return userPrincipal;
        }
        return null;
    }
}
