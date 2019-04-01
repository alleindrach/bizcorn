package allein.bizcorn.service.websocket.config;


import allein.bizcorn.service.websocket.CustomWebSocketHandlerDecoratorFactory;
import allein.bizcorn.service.websocket.WebSocketHandshakeHandler;
import allein.bizcorn.service.websocket.WebSocketHandshakeInterceptor;
import allein.bizcorn.service.websocket.msghandler.BinaryMessageHandler;
import allein.bizcorn.service.websocket.msghandler.TextMessageHandler;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Configuration
//@EnableWebSocket
//这个配置类不仅配置了 WebSocket，还配置了基于代理的 STOMP 消息
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Autowired
    private TextMessageHandler chatHandler;
    @Autowired
    private BinaryMessageHandler blobHandler;
    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    @Autowired
    private CustomWebSocketHandlerDecoratorFactory customWebSocketHandlerDecoratorFactory;
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(customWebSocketHandlerDecoratorFactory);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //定义了一个客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息
        config.enableSimpleBroker("/topic","/user","/group");

        config.setUserDestinationPrefix("/user/");
        //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀
        config.setApplicationDestinationPrefixes("/center");

    }
    @Override
    public boolean configureMessageConverters(
            List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(smt);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);

        converter.setObjectMapper(objectMapper);
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(new StringMessageConverter());
        messageConverters.add(new ByteArrayMessageConverter());
        messageConverters.add(converter);
        return false;
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

// 添加一个服务端点，来接收客户端的连接。将 “/gs-guide-websocket” 路径注册为 STOMP 端点。
// 这个路径与之前发送和接收消息的目的路径有所不同， 这是一个端点，
// 客户端在订阅或发布消息到目的地址前，要连接该端点，
// 即用户发送请求 ：url=’/127.0.0.1:8080/gs-guide-websocket 与 STOMP server 进行连接，之后再转发到订阅url
// withSockJS作用是添加SockJS支持

        registry.addEndpoint("/websocket").setHandshakeHandler(new WebSocketHandshakeHandler()).setAllowedOrigins("*").withSockJS();
    }

    //
//    @Override
////    重写 registerWebSocketHandlers 方法，这是一个核心实现方法，配置 websocket 入口，允许访问的域、注册 Handler、SockJs 支持和拦截器。
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
////      registry.addHandler()注册和路由的功能，当客户端发起 websocket 连接，把 /path 交给对应的 handler 处理，而不实现具体的业务逻辑，可以理解为收集和任务分发中心。
////        addInterceptors，顾名思义就是为 handler 添加拦截器，可以在调用 handler 前后加入我们自己的逻辑代码。
////        传入消息拦截器，主要负责用户的认证，如果无登录态，则直接拒绝连接 -->
//        registry.addHandler(chatHandler, "/message").addInterceptors(webSocketHandshakeInterceptor).setAllowedOrigins("*").withSockJS();
//        registry.addHandler(blobHandler, "/blob").addInterceptors(webSocketHandshakeInterceptor).setAllowedOrigins("*").withSockJS();;
//    }
////    添加对WebSocket的一些配置
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192*4);
        container.setMaxBinaryMessageBufferSize(8192*4);

        return container;
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println("recv : "+message);
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                return super.preSend(message, channel);
            }

        });
    }

    @Bean
    public MappingJackson2MessageConverter getMappingJackson2MessageConverter() {
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        //设置日期格式
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(smt);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        mappingJackson2MessageConverter.setObjectMapper(objectMapper);

        return mappingJackson2MessageConverter;
    }

}