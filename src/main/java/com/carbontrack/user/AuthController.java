package com.carbontrack.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class AuthController {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            AppUserRepository repository,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> body) {

        String name = body.getOrDefault("name", "").trim();
        String email = body.getOrDefault("email", "")
                .trim().toLowerCase();
        String password = body.getOrDefault("password", "");

        if (name.isBlank() || email.isBlank()
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || password.length() < 8
                || password.length() > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter a valid name and email, and a password of 8–72 characters."
            );
        }

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists."
            );
        }

        AppUser user = new AppUser();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));

        AppUser saved = repository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "id", saved.getId(),
                        "name", saved.getName(),
                        "email", saved.getEmail(),
                        "message", "Account created successfully."
                )
        );
    }

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> body,
            HttpServletRequest request,
            HttpServletResponse response) {

        String email = body.getOrDefault("email", "")
                .trim().toLowerCase();
        String password = body.getOrDefault("password", "");

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email, password
                        )
                );

        request.getSession(true);
        request.changeSessionId();

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        new HttpSessionSecurityContextRepository()
                .saveContext(context, request, response);

        AppUser user = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password."
                ));

        return Map.of(
                "id", user.getId().toString(),
                "name", user.getName(),
                "email", user.getEmail(),
                "message", "Login successful."
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request) {

        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully.")
        );
    }
}
