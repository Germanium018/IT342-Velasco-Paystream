package edu.cit.velasco.paystream.security;

import edu.cit.velasco.paystream.entity.Employee;
import edu.cit.velasco.paystream.entity.User;
import edu.cit.velasco.paystream.repository.EmployeeRepository;
import edu.cit.velasco.paystream.repository.UserRepository;
import edu.cit.velasco.paystream.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name"); // Format usually "John Doe"

        // Handle cases where GitHub email is private
        if (email == null) {
            email = oAuth2User.getAttribute("login") + "@github.com";
        }

        // 1. Check/Register User
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setProvider("GITHUB");
            user.setRole("ROLE_EMPLOYEE"); // Default role
            
            // Split name safely
            if (name != null && name.contains(" ")) {
                user.setFirstname(name.substring(0, name.indexOf(" ")));
                user.setLastname(name.substring(name.indexOf(" ") + 1));
            } else {
                user.setFirstname(name != null ? name : "GitHub");
                user.setLastname("User");
            }
            
            user = userRepository.save(user);

            // 2. Create Employee Profile (to prevent Dashboard white screens)
            Employee employee = new Employee();
            employee.setUser(user);
            employee.setPosition("UNASSIGNED");
            employee.setStatus("ACTIVE");
            employee.setBaseSalary(BigDecimal.ZERO);
            employee.setHireDate(LocalDate.now());
            employeeRepository.save(employee);
        } else {
            user = userOptional.get();
        }

        // 3. Generate JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        // 4. Redirect to React with the token in the URL
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}