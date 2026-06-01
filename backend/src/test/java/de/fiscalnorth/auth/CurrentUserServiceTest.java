package de.fiscalnorth.auth;

import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import de.fiscalnorth.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_returnsIdWhenAuthenticated() {
        User user = user(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        setPrincipal(user);

        assertThat(currentUserService.getCurrentUserId()).isEqualTo(42L);
    }

    @Test
    void getCurrentUser_throwsWhenNotAuthenticated() {
        assertThatThrownBy(() -> currentUserService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void isAuthenticated_returnsTrueForAppUserPrincipal() {
        setPrincipal(user(1L));
        assertThat(currentUserService.isAuthenticated()).isTrue();
    }

    @Test
    void isAuthenticated_returnsFalseWhenAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymous", null));
        assertThat(currentUserService.isAuthenticated()).isFalse();
    }

    private static User user(long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");
        user.setUserName("Test");
        user.setUserRole(UserRole.User);
        user.setPasswordHash("hash");
        return user;
    }

    private static void setPrincipal(User user) {
        AppUserPrincipal principal = new AppUserPrincipal(user);
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
