package com.spring_portfolio.mvc.user;

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


/*
User is a POJO, Plain Old Java Object.
First set of annotations add functionality to POJO
--- @Setter @Getter @ToString @NoArgsConstructor @RequiredArgsConstructor
The last annotation connect to database
--- @Entity
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"roles"})
@Convert(attributeName ="user", converter = JsonType.class)
public class User implements Comparable<User>{

    // automatic unique identifier for User record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // email, password, roles are key attributes to login and authentication
    @NotEmpty
    @Size(min=5)
    @Column(unique=true)
    @Email
    private String email;

    @NotEmpty
    private String password;

    // @NonNull, etc placed in params of constructor: "@NonNull @Size(min = 2, max = 30, message = "Name (2 to 30 chars)") String name"
    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @NotEmpty
    private String studyStatus = "NOTSTUDYING";

    // To be implemented
    @ManyToMany(fetch = EAGER)
    private Collection<UserRole> roles = new ArrayList<>();

    /* HashMap is used to store JSON for daily "stats"
    "stats": {
        "2022-11-13": {
            "calories": 2200,
            "steps": 8000
        }
    }
    */
    @Column
    private Double latitude;
    @Column 
    private Double longitude;

    @Column
	private String ImageEncoder;

    @Column
    private String role = roles.stream()
                          .map(UserRole::toString) // Ensure UserRole has a meaningful toString() method
                          .collect(Collectors.joining(", "));

    // Constructor used when building object from an API
    public User(String email, String password, String name, Date dob) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.ImageEncoder = null;
        this.studyStatus = "NOTSTUDYING";
        this.longitude = null;
    }

    public void addRole(UserRole role) {
        this.roles.add(role);
        if (this.role.equals("")) {
            this.role += ",";
        }
        this.role += role.toString();
    }
    // A custom getter to return age from dob attribute
    public int getAge() {
        if (this.dob != null) {
            LocalDate birthDay = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDay, LocalDate.now()).getYears(); }
        return -1;
    }
    public boolean hasRoleWithName(String roleName) {
        for (UserRole role : roles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int compareTo(User other) {
        return this.name.compareTo(other.name);
    }
    // Initialize static test data 
    public static User[] init() {

        // basics of class construction
        User p1 = new User();
        p1.setName("Thomas Edison");
        p1.setEmail("toby@gmail.com");
        p1.setPassword("123Toby!");
        p1.setStudyStatus("STUDYING");
        p1.setLatitude(38.9866435);
        p1.setLongitude(-76.9318851);
        // adding Note to notes collection
        try {  // All data that converts formats could fail
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1840");
            p1.setDob(d);
        } catch (Exception e) {
            // no actions as dob default is good enough
        }

        User p2 = new User();
        p2.setName("Hop");
        p2.setEmail("hop@gmail.com");
        p2.setPassword("123hop");
        p2.setStudyStatus("STUDYING");
        p2.setLatitude(35.3214);
        p2.setLongitude(-73.2341123);
        // adding Note to notes collection
        try {  // All data that converts formats could fail
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("04-02-2003");
            p2.setDob(d);
        } catch (Exception e) {
            // no actions as dob default is good enough
        }
        User p3 = new User();
        p3.setName("Owen");
        p3.setEmail("olin32@gmail.com");
        p3.setPassword("12346");
        p3.setStudyStatus("UNSTUDYING");
        p3.setLatitude(34.3214);
        p3.setLongitude(-74.2341123);
        try {  // All data that converts formats could fail
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("04-19-2006");
            p3.setDob(d);
        } catch (Exception e) {
            // no actions as dob default is good enough
        }
        // Array definition and data initialization
        User[] users = {p1, p2, p3};
        return users;
    }

    public static void main(String[] args) {
        // obtain User from initializer
        User users[] = init();

        // iterate using "enhanced for loop"
        for( User user : users) {
            System.out.println(user);  // print object
        }
    }

}