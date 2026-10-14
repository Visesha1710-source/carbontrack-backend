package com.carbontrack.profile;

import com.carbontrack.user.AppUser;
import com.carbontrack.user.AppUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(
    origins = "${app.cors.allowed-origins:http://localhost:3000}",
    allowCredentials = "true"
)
public class UserProfileController {

    private final UserProfileRepository repository;
    private final AppUserRepository userRepository;

    public UserProfileController(
            UserProfileRepository repository,
            AppUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private AppUser getCurrentUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public UserProfile getProfile(
            @AuthenticationPrincipal UserDetails principal) {

        AppUser user = getCurrentUser(principal.getUsername());

        return repository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile();
                    profile.setName(user.getName());
                    profile.setEmail(user.getEmail());
                    profile.setLocation("Hyderabad");
                    profile.setUser(user);
                    return repository.save(profile);
                });
    }

    @PutMapping
    public UserProfile saveProfile(
            @RequestBody UserProfile updated,
            @AuthenticationPrincipal UserDetails principal) {

        AppUser user = getCurrentUser(principal.getUsername());

        if (updated.getName() == null || updated.getName().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Name is required");
        }

        UserProfile profile = repository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    newProfile.setEmail(user.getEmail());
                    newProfile.setLocation("Hyderabad");
                    return newProfile;
                });

        profile.setUser(user);
        profile.setName(updated.getName().trim());

        // Keep the profile email tied to the authenticated account.
        profile.setEmail(user.getEmail());

        profile.setLocation(
            updated.getLocation() == null
                    || updated.getLocation().isBlank()
                ? "Hyderabad"
                : updated.getLocation().trim()
        );

        return repository.save(profile);
    }
}