package de.fiscalnorth.admin;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.shared.LocalizedException;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import de.fiscalnorth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @GetMapping("/users")
    public List<AdminUserSummary> listUsers() {
        requireAdmin();
        return userRepository.findAll().stream()
                .map(user -> new AdminUserSummary(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getUserRole() != null ? user.getUserRole().name() : UserRole.User.name()))
                .toList();
    }

    private void requireAdmin() {
        User user = currentUserService.getCurrentUser();
        if (user.getUserRole() != UserRole.Admin) {
            throw new LocalizedException("error.unauthorized");
        }
    }

    public record AdminUserSummary(Long id, String userName, String email, String role) {}
}
