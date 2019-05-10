package allein.bizcorn.servicefeign.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import feign.codec.Encoder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class FeignMultipartSupportConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    @Primary
    @Scope("prototype")
    public Encoder feignFormEncoder() {
        //这里不使用Feign-Spring-Form原生的Encoder，因为它处理file元素的方式是逐一放入map，而不是作为一个数组放入map，导致客户端不兼容，和service端也不兼容。
        return new FeignSpringFormEncoder();
    }
    @Bean
    public ErrorDecoder errorDecoder(){
        return new BizExceptionFeignErrorDecoder();
    }
    @Bean
    public Decoder feignDecoder() {

//        BizMappingJackson2HttpMessageConverter converter = new BizMappingJackson2HttpMessageConverter();
//        converter.setObjectMapper(objectMapper);
//
//        //不加会出现异常
//        //Could not extract response: no suitable HttpMessageConverter found for response type [class ]
//        MediaType[] mediaTypes = new MediaType[]{
//                MediaType.APPLICATION_JSON,
//                MediaType.APPLICATION_OCTET_STREAM,
//
//                MediaType.TEXT_HTML,
//                MediaType.TEXT_PLAIN,
//                MediaType.TEXT_XML,
//                MediaType.APPLICATION_STREAM_JSON,
//                MediaType.APPLICATION_ATOM_XML,
//                MediaType.APPLICATION_FORM_URLENCODED,
//                MediaType.APPLICATION_JSON_UTF8,
//                MediaType.APPLICATION_PDF,
//        };
//
//        converter.setSupportedMediaTypes(Arrays.asList(mediaTypes));
//        List<HttpMessageConverter<?>> customizedMessageConverters = Lists.newArrayList();
//        customizedMessageConverters.add(converter);//添加MappingJackson2HttpMessageConverter
//
//        //添加原有的剩余的HttpMessageConverter
//        List<HttpMessageConverter<?>> leftConverters = messageConverters.getObject().getConverters().stream()
//                .filter(x -> x.getClass().getName().equalsIgnoreCase(MappingJackson2HttpMessageConverter.class
//                        .getName()) == false)
//                .collect(Collectors.toList());
//
//        customizedMessageConverters.addAll(leftConverters);
        return new OptionalDecoder(new ResponseEntityDecoder(new FeignHeadersSpringDecoder(messageConverters.getObject().getConverters())));
    }
//    @Bean
//    public Decoder decoder(){
//        return  new HttpEn.Default();
//    }
}
