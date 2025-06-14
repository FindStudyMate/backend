package com.spring_portfolio.mvc.user;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private UserJpaRepository repository;

    @Autowired
    private DetailsService detailsService;

    @GetMapping("/")
    public ResponseEntity<List<User>> getPeople() {
        return new ResponseEntity<>(repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }
    
    @GetMapping("/onlyemail")
    public ResponseEntity<List<String>> returnEmail() {
        List<User> users = repository.findAll();
        List<String> result = new ArrayList<>();
        for (User user : users) {
            result.add(user.getEmail());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @PostMapping("/update/location") 
    public ResponseEntity<?> updateLocation(@RequestParam("email") String email, @RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude) {
        User user = repository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("User Not Found", HttpStatus.BAD_REQUEST);
        }
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        detailsService.save(user);
        return new ResponseEntity<>("Sucessfully updated", HttpStatus.OK);
    }
    @GetMapping("/get")
    public ResponseEntity<User> getPerson(@AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is not logged in
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String email = userDetails.getUsername(); // Email is mapped/unmapped to username for Spring Security

        // Find a person by username
        User user = repository.findByEmail(email);  

        // Return the person if found
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        Optional<User> optional = repository.findById(id);
        return optional.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<User> deleteUser(@RequestParam("email") String email) {
        User user = repository.findByEmail(email);
        if (user != null) {
            repository.deleteByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/createUser")
    public ResponseEntity<Object> postUser(@RequestBody User userRequest) {
        try {
            Date dob = userRequest.getDob();
            User user = new User(userRequest.getEmail(), userRequest.getPassword(), userRequest.getName(), dob);
            detailsService.save(user);
            detailsService.addRoleToUser(userRequest.getEmail(), "ROLE_USER");
            return new ResponseEntity<>(Map.of("message", userRequest.getEmail() + " is created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error processing the request."), HttpStatus.BAD_REQUEST);
        }
    }


   
    

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> userSearch(@RequestBody final Map<String, String> map) {
        String term = map.get("term");
        List<User> list = repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<Object> putUser(@RequestParam("email") String email, @RequestParam("password") String password) {
        User user = repository.findByEmail(email);
        if (user != null) {
            detailsService.changePassword(email, password);
            detailsService.save(user);
            return new ResponseEntity<>(Map.of("message", email + " is updated successfully"), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("error", "user not found"), HttpStatus.NOT_FOUND);
    }

    @PutMapping("/updateRole") 
    public ResponseEntity<Object> updateRole(@RequestParam("email") String email){
        User user = repository.findByEmail(email);
        if (user != null) {
            detailsService.addRoleToUser(email, "ROLE_ADMIN");
            detailsService.save(user);
            return new ResponseEntity<>(Map.of("message", email + " is updated successfully"), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("error", "user not found"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/createAdmin")
    public ResponseEntity<Object> postAdminuser(@RequestBody User userRequest) {
        try {
            Date dob = userRequest.getDob();
            User user = new User(userRequest.getEmail(), userRequest.getPassword(), userRequest.getName(), dob);
            detailsService.save(user);
            detailsService.addRoleToUser(userRequest.getEmail(), "ROLE_ADMIN");
            return new ResponseEntity<>(Map.of("message", userRequest.getEmail() + " is created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error processing the request."), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/studying")
    public ResponseEntity<ArrayList<User>> showStudyingUser() {
        List<User> users = repository.findAll();
        ArrayList<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getStudyStatus().equals("STUDYING")) {
                result.add(user);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    } 

    @PostMapping("/image/post")
    public ResponseEntity<String> saveImage(MultipartFile image, @RequestParam("email") String email) throws IOException {
        User existingFileOptional = repository.findByEmail(email);
        if (existingFileOptional != null) {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] bytearr = image.getBytes();
            String encodedString = encoder.encodeToString(bytearr);
            existingFileOptional.setImageEncoder(encodedString);
            detailsService.save(existingFileOptional);
            return new ResponseEntity<>("Image saved successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/updateStudy")
    public ResponseEntity<Map<String, String>> updateStudy(@RequestParam("email") String email, @RequestParam("study") String study) {
        User user = repository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(Map.of("error", "User not found"), HttpStatus.NOT_FOUND);
        }
        if (study.equals("STUDYING") || study.equals("NONSTUDYING")) {
            user.setStudyStatus(study);
            detailsService.save(user);
            return new ResponseEntity<>(Map.of("message", "Successfully updated"), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("error", "Not Correct Status"), HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/image/{email}")
    public ResponseEntity<?> downloadImage(@PathVariable String email) {
        User user = repository.findByEmail(email);
        if (user != null && user.getImageEncoder() != null) {
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(user.getImageEncoder());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(imageBytes);
        }
        return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/get/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        User optional = repository.findByEmail(email);
        if (optional != null) {
            return new ResponseEntity<>(optional, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
