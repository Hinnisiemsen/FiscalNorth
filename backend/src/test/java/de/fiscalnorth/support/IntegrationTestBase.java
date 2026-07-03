package de.fiscalnorth.support;

import de.fiscalnorth.auth.AppUserPrincipal;
import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import de.fiscalnorth.user.repository.UserRepository;
import de.fiscalnorth.user.service.UserOnboardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserOnboardingService userOnboardingService;

    protected RequestPostProcessor authenticatedUser() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User user = new User();
        user.setUserName("IT User " + suffix);
        user.setEmail("it-" + suffix + "@fiscalnorth.test");
        user.setPasswordHash(passwordEncoder.encode("TestPass123!"));
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setUserRole(UserRole.User);
        user = userRepository.save(user);
        userOnboardingService.seedDefaultCategories(user);
        return user(new AppUserPrincipal(user));
    }
}
