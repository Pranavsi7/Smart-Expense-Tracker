package com.project.expensetrackerapi.resources;

import com.project.expensetrackerapi.config.JwtConfig;
import com.project.expensetrackerapi.domain.User;
import com.project.expensetrackerapi.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Authentication", description = "User registration and login")
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Returns a JWT token on successful authentication.")
    @ApiResponse(responseCode = "200", description = "Login successful, JWT returned")
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> userMap) {
        String email    = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        User user = userService.validateUser(email, password);
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Registration successful, JWT returned")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, Object> userMap) {
        String firstName = (String) userMap.get("firstName");
        String lastName  = (String) userMap.get("lastName");
        String email     = (String) userMap.get("email");
        String password  = (String) userMap.get("password");
        User user = userService.registerUser(firstName, lastName, email, password);
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    private Map<String, String> generateJWTToken(User user) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder()
                .signWith(jwtConfig.getSigningKey(), SignatureAlgorithm.HS256)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + jwtConfig.getExpiration()))
                .claim("userId",    user.getUserId())
                .claim("email",     user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName",  user.getLastName())
                .compact();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }
}
