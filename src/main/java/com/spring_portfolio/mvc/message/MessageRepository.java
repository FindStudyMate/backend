package com.spring_portfolio.mvc.message;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface MessageRepository extends JpaRepository<Message, Long> {
    
}
