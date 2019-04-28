package allein.bizcorn.service.security;

import allein.bizcorn.model.entity.Authority;

import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.service.db.mysql.dao.UserDAO;
import allein.bizcorn.service.facade.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired  //数据库服务类
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //SUser对应数据库中的用户表，是最终存储用户和密码的表，可自定义
        //本例使用SUser中的email作为用户名:
        IUser user = userService.getUserByUsername(userName).getData();
        if (user == null) {
            throw new UsernameNotFoundException("用户名密码错误！");
        }
        // SecurityUser实现UserDetails并将SUser的Email映射为username
        ArrayList<SimpleGrantedAuthority> authorities= new ArrayList<SimpleGrantedAuthority>();
        List<String> authoritiesInDB=   userService.getUserAuthorities(user.getId()).getData();
        if(authoritiesInDB!=null && authoritiesInDB.size()>0)
        {
            for (String authority:authoritiesInDB) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        else
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        User userDetails = new User(user.getUsername(),user.getPassword(),authorities);
        return userDetails;

    }

}
