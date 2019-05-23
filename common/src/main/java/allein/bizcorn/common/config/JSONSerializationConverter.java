/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-21 09:48
 **/
public class JSONSerializationConverter extends AbstractHttpMessageConverter<Object> {
    private Logger logger = LoggerFactory.getLogger(JSONSerializationConverter.class);

    public JSONSerializationConverter() {
        // 构造方法中指明consumes（req）和produces（resp）的类型，指明这个类型才会使用这个converter
        super(new MediaType("application", "json", Charset.forName("UTF-8")));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // 使用Serializable，这里可以直接返回true
        // 使用object，这里还要加上Serializable接口实现类判断
        // 根据自己的业务需求加上其他判断
        return true;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        String  jsonString = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
        // base64使得二进制数据可视化，便于测试

        try {
            JSONObject jso=JSON.parseObject(jsonString);
            return jso;
        } catch (Exception e) {
            logger.error("exception when java deserialize, the input is:{}", jsonString, e);
            return null;
        }
    }

    @Override
    protected void writeInternal(Object t, HttpOutputMessage outputMessage)
            throws IOException {

        String jsonString=JSON.toJSONString(t);

        // base64使得二进制数据可视化，便于测试
        outputMessage.getBody().write(jsonString.getBytes());
    }

}