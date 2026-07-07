package de.fiscalnorth.user.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.auth.UnauthorizedException;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.user.dto.ChangePasswordRequest;
import de.fiscalnorth.user.dto.CreateUserRequest;
import de.fiscalnorth.user.dto.UpdateUserProfileRequest;
import de.fiscalnorth.user.dto.UserProfileDto;
import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
import de.fiscalnorth.billing.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final EntitlementService entitlementService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserProfileDto getCurrentUserProfile() {
        return toProfileDto(currentUserService.getCurrentUser());
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("User", "id", id));
    }

    @Transactional
    public UserProfileDto updateCurrentUserProfile(UpdateUserProfileRequest request) {
        User user = currentUserService.getCurrentUser();
        user.setUserName(request.userName());
        user.setLocale(request.locale());
        return toProfileDto(userRepository.save(user));
    }

    @Transactional
    public void changeCurrentUserPassword(ChangePasswordRequest request) {
        User user = currentUserService.getCurrentUser();
        if (user.getAuthProvider() == AuthProvider.GOOGLE) {
            throw new IllegalArgumentException("Password change is not available for Google-only accounts");
        }
        if (user.getPasswordHash() == null
                || !passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setUserRole(request.userRole());
        user.setAuthProvider(AuthProvider.LOCAL);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RessourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    public UserProfileDto toProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getAuthProvider(),
                user.getLocale() != null ? user.getLocale() : "en",
                entitlementService.toSummary(user));
    }
}
