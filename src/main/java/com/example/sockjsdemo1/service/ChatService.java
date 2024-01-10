package com.example.sockjsdemo1.service;


import com.alibaba.fastjson.JSONObject;
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


    public Boolean sendMsg(String msg) {
        try {
            JSONObject msgJson = JSONObject.parseObject(msg);
            if (msgJson.getString("to").equals("all") && msgJson.getString("type").equals(ChatMessage.MessageType.CHAT.toString())){
                simpMessageSendingOperations.convertAndSend("/topic/public", msgJson);

            }else if (msgJson.getString("to").equals("all") && msgJson.getString("type").equals(ChatMessage.MessageType.JOIN.toString())) {
                simpMessageSendingOperations.convertAndSend("/topic/public", msgJson);

            }else if(msgJson.getString("to").equals("all") &&  msgJson.getString("type").equals(ChatMessage.MessageType.LEAVE.toString())) {
                simpMessageSendingOperations.convertAndSend("/topic/public", msgJson);

            }else if (!msgJson.getString("to").equals("all") &&  msgJson.getString("type").equals(ChatMessage.MessageType.CHAT.toString())){
                try {
//                    simpMessageSendingOperations.convertAndSend("/aaa", msgJson);
                    simpMessageSendingOperations.convertAndSendToUser(msgJson.getString("to"),"/topic/"+msgJson.getString("to"), msgJson);
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