/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.exception;

public class UserNotLoginException extends ErrorCodeException {

    /**
     *
     */
    private static final long serialVersionUID = -3837659312079505063L;
    public UserNotLoginException(ExceptionEnum exCode) {
        super(exCode.code(), exCode.desc());
    }
    public UserNotLoginException() {
        super(ExceptionEnum.USER_NOT_LOGIN.code(), ExceptionEnum.USER_NOT_LOGIN.desc());
    }

}
