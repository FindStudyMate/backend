package com.spring_portfolio.mvc.message;
import static jakarta.persistence.FetchType.EAGER;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="user", converter = JsonType.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty
    @Column(unique=true)
    private ArrayList<String> chatWho;

    @Column(columnDefinition = "json")
    @Type(JsonType.class)
    private Multimap<String, String> chatlog;

    public Message(String name1, String name2) {
        chatWho = new ArrayList<>();
        chatWho.add(name1);
        chatWho.add(name2);
        chatlog =  ArrayListMultimap.create();
    }
    public Message(String name1, String name2, String name3) {
        chatWho = new ArrayList<>();
        chatWho.add(name1);
        chatWho.add(name2);
        chatWho.add(name3);
        chatlog =  ArrayListMultimap.create();
    }
    public Message(String name1, String name2, String name3, String name4) {
        chatWho = new ArrayList<>();
        chatWho.add(name1);
        chatWho.add(name2);
        chatWho.add(name3);
        chatWho.add(name4);
        chatlog =  ArrayListMultimap.create();
    }
    public String getNames() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < chatWho.size(); i++) {
            result.append(chatWho.get(i));
            if (i != chatWho.size()-1) {
                result.append(",");
            }
        }
        return result.toString();
    }
    public void addChat(String who, String message) {
        chatlog.put(who, message);
    }
    public static Message[] init() {
        Message chat1 = new Message("Owen", "Hop");
        chat1.addChat("Owen", "Hello");
        chat1.addChat("Hop", "Ye hello");
        chat1.addChat("Owen", "Nice to meet you");
        Message chat2 = new Message("Owen", "Hop", "Thomas Edison");
        chat2.addChat("Owen", "Hello1");
        chat2.addChat("Hop", "Ye hello1");
        chat2.addChat("Thomas Edison", "Nice to meet you1");
        Message[] messages = {chat1, chat2};
        return messages;
    }
}
