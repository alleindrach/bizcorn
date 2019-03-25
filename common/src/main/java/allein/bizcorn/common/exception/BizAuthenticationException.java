package allein.bizcorn.common.exception;

import org.springframework.security.core.AuthenticationException;

public class BizAuthenticationException extends AuthenticationException {
    public BizAuthenticationException(String msg) {
        super(msg);
    }
}
