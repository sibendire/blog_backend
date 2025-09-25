package com.blogger.blog.controller;

import com.blogger.blog.entities.AppUser;
import com.blogger.blog.entities.Confirmation;
import com.blogger.blog.entities.Roles;
import com.blogger.blog.repositories.AppUserRepository;
import com.blogger.blog.repositories.ConfirmationRepository;
import com.blogger.blog.repositories.RoleRepository;
import com.blogger.blog.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthRestController {

    private final AppUserRepository appUserRepository;
    private final ConfirmationRepository confirmationRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @PostMapping("/signup")
    public String signup(@RequestBody AppUser appUser) {
        // Encrypt password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        appUser.setPassword(encoder.encode(appUser.getPassword()));
        appUser.setConfirmPassword(encoder.encode(appUser.getConfirmPassword()));
        appUser.setIsEnabled(false);

        appUser.setVerificationCode(UUID.randomUUID().toString());

        // Assign default role
        Roles defaultRole = roleRepository.findByName("ROLE_USER");
        if (defaultRole == null) {
            throw new RuntimeException("Default role not found.");
        }
        appUser.setRoles(Set.of(defaultRole));


        // Save
        appUserRepository.save(appUser);

        // Save token
        Confirmation confirmation = new Confirmation();
        confirmation.setUser(appUser);
        confirmation.setToken(appUser.getVerificationCode());
        confirmationRepository.save(confirmation);

        // Send verification email
        String verifyLink = "http://localhost:8080/api/auth/verify?token=" + appUser.getVerificationCode();
        emailService.sendMimeMessage(
                appUser.getFirstName(),
                appUser.getEmail(),
                "Verify your account",
                "Hello " + appUser.getFirstName() + ",\nPlease verify your account: " + verifyLink
        );

        return "Signup successful. Please check your email for verification.";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        if (confirmation == null) {
            return "Invalid or expired token.";
        }

        AppUser user = confirmation.getUser();
        user.setIsEnabled(true);
        appUserRepository.save(user);

        confirmationRepository.delete(confirmation);

        return "Account verified. You can now login.";
    }
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        AppUser user = appUserRepository.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("success", false);
            response.put("message", "User not found.");
            return response;
        }

        // check if enabled
        if (!Boolean.TRUE.equals(user.getIsEnabled())) {
            response.put("success", false);
            response.put("message", "Please verify your email before login.");
            return response;
        }

        // verify password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid password.");
            return response;
        }

        // success
        response.put("success", true);
        response.put("message", "Login successful.");
        response.put("user", user); // or return a DTO without password field
        return response;
    }

}
