package com.spring_portfolio.mvc.message;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
public class MessageDetailService {
    @Autowired MessageRepository repository;
    public Message findGroupChat2(String name1, String name2) {
        List<Message> messages = repository.findAll();
        for (Message message : messages) {
            if (message.getChatWho().size() == 2) {
                if (message.getChatWho().contains(name1) && message.getChatWho().contains(name2)) {
                    return message;
                }
            }
        }
        return null;
    }
    public Message findGroupChat3(String name1, String name2, String name3) {
        List<Message> messages = repository.findAll();
        for (Message message : messages) {
            if (message.getChatWho().size() == 3) {
                if (message.getChatWho().contains(name1) && message.getChatWho().contains(name2) && message.getChatWho().contains(name3)) {
                    return message;
                }
            }
        }
        return null;
    }
    public Message findGroupChat4(String name1, String name2, String name3, String name4) {
        List<Message> messages = repository.findAll();
        for (Message message : messages) {
            if (message.getChatWho().size() == 4) {
                if (message.getChatWho().contains(name1) && message.getChatWho().contains(name2) && message.getChatWho().contains(name3) && message.getChatWho().contains(name4)) {
                    return message;
                }
            }
        }
        return null;
    }
}
