package allein.bizcorn.service.security;


import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;

import allein.bizcorn.common.model.entity.user.User;

import allein.bizcorn.service.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired  //数据库服务类
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String userName) throws CommonException {
        //SUser对应数据库中的用户表，是最终存储用户和密码的表，可自定义
        //本例使用SUser中的email作为用户名:
        User user = userDAO.selectByNameCached(userName);

        if (user == null) {

            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);

        }

        // SecurityUser实现UserDetails并将SUser的Email映射为username
        allein.bizcorn.service.security.UserDetails userDetails = new allein.bizcorn.service.security.UserDetails(user);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return userDetails;

    }

}