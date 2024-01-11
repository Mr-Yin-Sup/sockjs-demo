package com.example.sockjsdemo1.service;


import com.alibaba.fastjson.JSON;
import com.example.sockjsdemo1.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * @Author : JCccc
 * @CreateTime : 2020/8/26
 * @Description :
 **/
@Service
public class ChatService {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;


    public Boolean sendMsg(ChatMessage msg) {
        try {

            if (msg.getTo().equals("all") && msg.getType().equals(ChatMessage.MessageType.CHAT)){
                simpMessageSendingOperations.convertAndSend("/topic/public", JSON.toJSONString(msg));

            }else if (msg.getTo().equals("all") && msg.getType().equals(ChatMessage.MessageType.JOIN)) {
                simpMessageSendingOperations.convertAndSend("/topic/public", JSON.toJSONString(msg));

            }else if(msg.getTo().equals("all") &&  msg.getType().equals(ChatMessage.MessageType.LEAVE)) {
                simpMessageSendingOperations.convertAndSend("/topic/public", JSON.toJSONString(msg));

            }else if (!msg.getTo().equals("all") &&  msg.getType().equals(ChatMessage.MessageType.CHAT)){
                try {
//                    simpMessageSendingOperations.convertAndSend("/aaa", msgJson);
                    simpMessageSendingOperations.convertAndSendToUser(msg.getTo(),"/topic/"+msg.getTo(), JSON.toJSONString(msg));
//                    System.out.println(MessageFormat.format("Message sent to user: {0}", msgJson.toString()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


}