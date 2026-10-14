
package com.carbontrack.goal;

import com.carbontrack.user.AppUser;
import com.carbontrack.user.AppUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class GoalController {

    private final GoalRepository repository;
    private final AppUserRepository userRepository;

    public GoalController(
            GoalRepository repository,
            AppUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private AppUser getCurrentUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void initializeStarterGoals(AppUser user) {
        if (user.isStarterGoalsInitialized()) {
            return;
        }

        // Preserve older goals that have not yet been assigned
        // to a user. This assumes the existing data belongs to
        // the first account being migrated.
        List<Goal> unassigned = repository.findAllByUserIsNull();

        for (Goal goal : unassigned) {
            goal.setUser(user);
        }

        if (!unassigned.isEmpty()) {
            repository.saveAll(unassigned);
        }

        String[][] starters = {
            {
                "Choose greener transport",
                "Complete 12 trips using public transport, cycling, or walking."
            },
            {
                "Save electricity",
                "Aim to reduce your electricity use by 10%."
            },
            {
                "Reduce single-use plastic",
                "Avoid 20 single-use plastic items by choosing reusable alternatives."
            },
            {
                "Eat more plant-based meals",
                "Enjoy 12 plant-based meals."
            },
            {
                "Recycle more",
                "Recycle or properly sort 15 recyclable items."
            },
            {
                "Complete eco-friendly actions",
                "Complete five practical actions that reduce your environmental impact."
            }
        };

        for (String[] starter : starters) {
            Goal goal = new Goal();
            goal.setTitle(starter[0]);
            goal.setDetail(starter[1]);
            goal.setProgress(0);
            goal.setUser(user);
            repository.save(goal);
        }

        user.setStarterGoalsInitialized(true);
        userRepository.save(user);
    }

    @GetMapping
    public List<Goal> getAllGoals(
            @AuthenticationPrincipal UserDetails principal) {

        AppUser user = getCurrentUser(principal.getUsername());
        initializeStarterGoals(user);

        return repository.findAllByUser_EmailIgnoreCase(
                principal.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Goal createGoal(
            @RequestBody Goal goal,
            @AuthenticationPrincipal UserDetails principal) {

        AppUser user = getCurrentUser(principal.getUsername());
        goal.setUser(user);

        return repository.save(goal);
    }

    @PutMapping("/{id}")
    public Goal updateGoal(
            @PathVariable Long id,
            @RequestBody Goal updatedGoal,
            @AuthenticationPrincipal UserDetails principal) {

        Goal existing = repository
                .findByIdAndUser_EmailIgnoreCase(
                        id, principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"));

        existing.setTitle(updatedGoal.getTitle());
        existing.setDetail(updatedGoal.getDetail());
        existing.setProgress(updatedGoal.getProgress());

        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        Goal goal = repository
                .findByIdAndUser_EmailIgnoreCase(
                        id, principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"));

        repository.delete(goal);
    }
}
