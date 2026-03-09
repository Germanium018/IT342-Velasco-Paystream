package edu.cit.velasco.paystream.controller;

import edu.cit.velasco.paystream.entity.User;
import edu.cit.velasco.paystream.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Vite Port
public class AuthController {

    private final AuthService authService;

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
                .map(user -> ResponseEntity.ok(Map.of("success", true, "user", user)))
                .orElse(ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials")));
    }
}