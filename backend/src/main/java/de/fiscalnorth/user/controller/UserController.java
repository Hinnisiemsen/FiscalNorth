package de.fiscalnorth.user.controller;

import de.fiscalnorth.user.dto.ChangePasswordRequest;
import de.fiscalnorth.user.dto.UpdateUserProfileRequest;
import de.fiscalnorth.user.dto.UserProfileDto;
import de.fiscalnorth.user.model.UserRole;
import de.fiscalnorth.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateCurrentUser(@RequestBody @Valid UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUserProfile(request));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changeCurrentUserPassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<de.fiscalnorth.user.dto.UserProfileDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream()
                .map(userService::toProfileDto)
                .toList());
    }
}
