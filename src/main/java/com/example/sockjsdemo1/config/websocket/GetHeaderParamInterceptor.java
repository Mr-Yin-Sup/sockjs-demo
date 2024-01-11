package com.example.sockjsdemo1.config.websocket;


import com.example.sockjsdemo1.model.ChatMessage;
import com.example.sockjsdemo1.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : JCccc
 * @CreateTime : 2020/8/26
 * @Description :
 **/
@Component
public class GetHeaderParamInterceptor extends ChannelInterceptorAdapter {

    public static ConcurrentHashMap<String,UserPrincipal> sockMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,String> sockSessionMap = new ConcurrentHashMap<>();

    @Autowired
    @Lazy
    private ChatService chatService;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Map<String,?> raw = (Map<String,?>)message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
            String sessionId = accessor.getSessionId();
            if (sockSessionMap.keySet().contains(sessionId)) return message;
            else throw new RuntimeException("没有权限");
        }
        if (StompCommand.SEND.equals(accessor.getCommand())){
            String sessionId = accessor.getSessionId();
            if (sockSessionMap.keySet().contains(sessionId)) return message;
            else throw new RuntimeException("没有权限");
        }
        String username = null;
        try {
            if (raw instanceof Map) {
                List<String> list = (List<String>)raw.get("username");
                username = list.get(0);
            }

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }


        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            if (raw instanceof Map) {
                // 设置当前访问的认证用户
                UserPrincipal userPrincipal = new UserPrincipal(username);
                accessor.setUser(userPrincipal);
                sockMap.put(userPrincipal.getName(), userPrincipal);
                sockSessionMap.put(accessor.getSessionId(), username);

            }
        }else if(SimpMessageType.HEARTBEAT.equals(accessor.getMessageType()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())){ //心跳检测或者鉴权
            System.out.println(sockSessionMap.get(accessor.getSessionId())+"用户心跳检测");
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())){//断开连接
            System.out.println(sockSessionMap.get(accessor.getSessionId())+"用户退出聊天室");
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSender(sockSessionMap.get(accessor.getSessionId()));
            chatMessage.setTo("all");
            chatMessage.setContent(sockSessionMap.get(accessor.getSessionId())+"退出了聊天室");
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatService.sendMsg(chatMessage);
            sockMap.remove(sockSessionMap.get(accessor.getSessionId()));
            sockSessionMap.remove(accessor.getSessionId());

        }
        return message;
    }
}