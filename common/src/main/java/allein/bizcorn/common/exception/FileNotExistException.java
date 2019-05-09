/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.exception;

public class FileNotExistException extends ErrorCodeException {

    /**
     *
     */
    private static final long serialVersionUID = -3837659312079505063L;

    public FileNotExistException(ExceptionEnum exCode) {
        super(exCode.code(), exCode.desc());
    }
    public FileNotExistException() {
        super(ExceptionEnum.FILE_NOT_EXISTS.code(),ExceptionEnum.FILE_NOT_EXISTS.desc());
    }
}
