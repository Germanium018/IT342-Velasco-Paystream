package edu.cit.velasco.paystream.controller;

import edu.cit.velasco.paystream.entity.User;
import edu.cit.velasco.paystream.security.JwtUtils;
import edu.cit.velasco.paystream.service.AuthService;
import edu.cit.velasco.paystream.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Vite Port
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = new User();
            user.setEmail(body.get("email"));
            user.setFirstname(body.get("firstname"));
            user.setLastname(body.get("lastname"));
            user.setRole(body.get("role"));
            
            authService.registerUser(user, body.get("password"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        return authService.loginUser(body.get("email"), body.get("password"))
                .map(user -> {
                    // Load UserDetails needed for JWT generation
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
                    
                    // Generate the actual token
                    String token = jwtUtils.generateToken(userDetails);

                    // Return success response with token and role
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "token", token,
                        "role", user.getRole(),
                        "user", user
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of(
                    "success", false, 
                    "message", "Invalid credentials"
                )));
    }
}