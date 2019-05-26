/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-25 22:27
 **/
public class BizResponseEntity<T> extends ResponseEntity<T> implements Serializable{

    public BizResponseEntity(HttpStatus status) {
        super(status);
    }

    public BizResponseEntity(@Nullable T body, HttpStatus status) {
        super(body, status);
    }

    public BizResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
        super(headers, status);
    }

    public BizResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
    }
}
