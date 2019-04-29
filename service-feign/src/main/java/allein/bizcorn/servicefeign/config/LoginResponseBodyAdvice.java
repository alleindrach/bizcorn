package allein.bizcorn.servicefeign.config;

import allein.bizcorn.model.output.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.PostConstruct;

@ControllerAdvice
public class LoginResponseBodyAdvice implements ResponseBodyAdvice<Result> {

    @PostConstruct
    public void init() throws Exception {

    }

    /**
     * 判断支持的类型
     *
     * @param returnType
     * @param converterType
     * @return
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice#supports(org.springframework.core.MethodParameter,
     *      java.lang.Class)
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod().getReturnType().isAssignableFrom(Result.class);
    }

    /**
     *
     *
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice#beforeBodyWrite(java.lang.Object,
     *      org.springframework.core.MethodParameter,
     *      org.springframework.http.MediaType, java.lang.Class,
     *      org.springframework.http.server.ServerHttpRequest,
     *      org.springframework.http.server.ServerHttpResponse)
     */
    @Override
    public Result beforeBodyWrite(Result body, MethodParameter returnType,
                                  org.springframework.http.MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        return body;
    }

}
