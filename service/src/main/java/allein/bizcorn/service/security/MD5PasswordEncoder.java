package allein.bizcorn.service.security;
import org.springframework.util.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MD5PasswordEncoder  implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        //对密码进行 md5 加密
        String md5Password = DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());
        return null;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String md5Password = DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());
        return md5Password.compareToIgnoreCase(encodedPassword)==0;
    }
}
