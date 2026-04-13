package edu.cit.velasco.paystream.controller;

import edu.cit.velasco.paystream.entity.User;
import edu.cit.velasco.paystream.repository.UserRepository;
import edu.cit.velasco.paystream.security.JwtUtils;
import edu.cit.velasco.paystream.service.AuthService;
import edu.cit.velasco.paystream.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") 
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository; // Added for /me lookup

    /**
     * GET /me
     * Used by React after GitHub login to fetch user profile & role using the JWT.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // 1. Extract authentication from context (set by JwtRequestFilter)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
            }

            String email = authentication.getName();

            // 2. Fetch user details from Supabase
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. Return role and user object to satisfy OAuth2RedirectHandler.jsx
            return ResponseEntity.ok(Map.of(
                    "role", user.getRole(),
                    "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = new User();
            user.setEmail(body.get("email"));
            user.setFirstname(body.get("firstname"));
            user.setLastname(body.get("lastname"));
            user.setRole(body.get("role"));
            user.setProvider("LOCAL"); // Mark as standard registration
            
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
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
                    String token = jwtUtils.generateToken(userDetails);

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