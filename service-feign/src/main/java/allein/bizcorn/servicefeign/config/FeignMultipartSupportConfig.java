package allein.bizcorn.servicefeign.config;


import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import feign.codec.Encoder;
@Configuration
public class FeignMultipartSupportConfig {
    @Bean
    public Encoder feignFormEncoder() {
        return new FeignSpringFormEncoder();
//        return new SpringFormEncoder();
    }
    @Bean
    public ErrorDecoder errorDecoder(){
        return new BizExceptionFeignErrorDecoder();
    }
//
//    @Bean
//    @Primary
//    @Scope("prototype")
//    public Encoder multipartFormEncoder() {
//        return new SpringFormEncoder();
//    }
//
//    @Bean
//    public feign.Logger.Level multipartLoggerLevel() {
//        return feign.Logger.Level.FULL;
//    }
}
