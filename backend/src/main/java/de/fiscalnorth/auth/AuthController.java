package de.fiscalnorth.auth;

import de.fiscalnorth.auth.dto.AuthStatusDto;
import de.fiscalnorth.auth.dto.LoginRequest;
import de.fiscalnorth.auth.dto.RegisterRequest;
import de.fiscalnorth.user.dto.UserProfileDto;
import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import de.fiscalnorth.user.repository.UserRepository;
import de.fiscalnorth.user.service.UserOnboardingService;
import de.fiscalnorth.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import de.fiscalnorth.auth.dto.CsrfTokenResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserOnboardingService userOnboardingService;
    private final CurrentUserService currentUserService;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @GetMapping("/status")
    public ResponseEntity<AuthStatusDto> status() {
        if (!currentUserService.isAuthenticated()) {
            return ResponseEntity.ok(new AuthStatusDto(false, null));
        }
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(new AuthStatusDto(true, user.getAuthProvider()));
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDto> register(
            @RequestBody @Valid RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setUserRole(UserRole.User);
        user = userRepository.save(user);
        userOnboardingService.seedDefaultCategories(user);

        authenticateSession(user.getEmail(), request.password(), httpRequest, httpResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.toProfileDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserProfileDto> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        authenticateSession(request.email(), request.password(), httpRequest, httpResponse);
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return ResponseEntity.ok(userService.toProfileDto(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        request.getSession(false);
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csrf")
    public CsrfTokenResponse csrf(org.springframework.security.web.csrf.CsrfToken token) {
        return new CsrfTokenResponse(token.getToken(), token.getHeaderName());
    }

    private void authenticateSession(String email, String password,
                                     HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
    }
}
