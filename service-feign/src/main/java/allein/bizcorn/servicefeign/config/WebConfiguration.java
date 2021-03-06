/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.servicefeign.config;

import allein.bizcorn.common.config.JSONSerializationConverter;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-20 23:39
 **/
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
//                .indentOutput(true)
//                .modulesToInstall(new ParameterNamesModule());
//        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
        converters.add(0,new JSONSerializationConverter());
    }
}