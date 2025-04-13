package com.spring_portfolio.mvc.message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/chat")
public class MessageApiController {
    @Autowired
    private MessageDetailService detailService;

    @Autowired
    private MessageRepository repository;

    @GetMapping("/{name}")
    public ResponseEntity<ArrayList<Message>> getMessage(@PathVariable String name) {
        List<Message> chats = repository.findAll();
        ArrayList<Message> result = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            if (chats.get(i).getChatWho().contains(name)) {
                result.add(chats.get(i));
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @PostMapping("/addChat2")
    public ResponseEntity<?> addChat(@PathVariable String name) {
        Message added = detailService.findGroupChat2(name, name)
    }
}
