package com.gis.gis.controllers;

import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gis.gis.exceptions.BadRequestException;
import com.gis.gis.models.Users;
import com.gis.gis.payloads.request.EncryptedRequest;
import com.gis.gis.payloads.request.usersss;
import com.gis.gis.payloads.response.EncryptedResponse;
import com.gis.gis.payloads.response.LoginResponse;
import com.gis.gis.repositories.UserRepo;
import com.gis.gis.services.UserService;
import com.gis.gis.utils.Json;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    UserRepo userrepo;
    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    UserService userService;
    private static final String SECRET_KEY = "123@";

    @PostMapping(value = "/Registeruser")
    public <json> EncryptedResponse registerUser(@RequestBody EncryptedRequest req)
            throws Exception {
        Users register = Json.deserialize(Users.class, req.data());
        // Users user = new Users();
        String username = register.getUsername();
        String password = register.getPassword();
        String role = register.getRole();
        String email = register.getEmail();
        String secretKey = register.getSecretKey();
        if ("admin".equals(role) && !SECRET_KEY.equals(secretKey)) {
            throw new BadRequestException("Invalid secret key for admin");
        }

        if (userrepo.existsByUsername(username)) {
            throw new BadRequestException("Username already exists!");
        }
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRole(role);
        user.setEmail(email);
        userrepo.save(user);

        return new EncryptedResponse("Registered");
    }

    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody Users loginRequest) {
    // String username = loginRequest.getUsername();

    // String password = loginRequest.getPassword();

    // Users user = userRepo.findByUsername(username);
    // if (user == null || !user.getPassword().equals(password)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
    // // Invalid credentials
    // }
    // // return new ResponseEntity<>(user, HttpStatus.OK);
    // return ResponseEntity.ok(user);
    // }

    @PostMapping("/login")
    public <json> EncryptedResponse signIn(@Valid @RequestBody EncryptedRequest req)
            throws Exception {

        Users loginRequest = Json.deserialize(Users.class, req.data());

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // String otp = loginRequest.otp();
        // String captcha = loginRequest.captcha();
        String fp = loginRequest.getFp();

        userService.validateUsername(username);
        Users user = userService.authenticateUsers(username, password);

        String jwt = userService.generateJwt(user, fp);
        return new EncryptedResponse(new LoginResponse(jwt, user));
    }

    @PutMapping("/edituser/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users formmodel)
            throws Exception {
        Optional<Users> optionalUser = userrepo.findById(id);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            // Update user details
            user.setUsername(formmodel.getUsername());
            user.setEmail(formmodel.getEmail());
            user.setEmail(formmodel.getPassword());

            // Save updated user
            final Users updatedUser = userrepo.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            // User not found, return 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    // @GetMapping("/get-user/{id}")
    // public ResponseEntity<Users> getUserById(@PathVariable Long id) {
    // Optional<Users> optionalUser = userrepo.findById(id);
    // if (optionalUser.isPresent()) {
    // Users user = optionalUser.get();
    // return ResponseEntity.ok(user);
    // } else {
    // return ResponseEntity.notFound().build();
    // }
    // }

    @PostMapping("/get-user-by-id")
    public EncryptedResponse getUserById(@Valid @RequestBody EncryptedRequest req)
            throws Exception {
        // Users body = req.bodyAs(Users.class);
        Users body = Json.deserialize(Users.class, req.data());
        Optional<Users> result = userrepo.findById(body.getId());
        return new EncryptedResponse(result);
    }

    @GetMapping("/userlist")
    public List<Map<String, Object>> userlist() throws Exception {
        List<Map<String, Object>> fm = userrepo.getUsers();

        return (fm);
    }

    // @PostMapping("/userlist")
    // public EncryptedResponse getUsers(@RequestBody EncryptedRequest req)
    // throws Exception {
    // Users body = Json.deserialize(Users.class, req.data());
    // List<Map<String, Object>> result = userrepo.getUsers(body.getId());
    // return new EncryptedResponse(result);
    // }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userrepo.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }
}
