package com.spring_portfolio.mvc.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
Extends the JpaRepository interface from Spring Data JPA.
-- Java Persistent API (JPA) - Hibernate: map, store, update and retrieve database
-- JpaRepository defines standard CRUD methods
-- Via JPA the developer can retrieve database from relational databases to Java objects and vice versa.
 */
public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findAllByOrderByNameAsc();

    // JPA query, findBy does JPA magic with "Name", "Containing", "Or", "Email", "IgnoreCase"
    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    /* Custom JPA query articles, there are articles that show custom SQL as well
       https://springframework.guru/spring-data-jpa-query/
       https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods
    */
    User findByEmailAndPassword(String email, String password);

    // Custom JPA query
    @Query(
            value = "SELECT * FROM User p WHERE p.name LIKE ?1 or p.email LIKE ?1",
            nativeQuery = true)
    List<User> findByLikeTermNative(String term);
    /*
      https://www.baeldung.com/spring-data-jpa-query
    */

    void deleteByEmail(String email);
}