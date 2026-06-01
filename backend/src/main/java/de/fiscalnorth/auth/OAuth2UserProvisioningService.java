package de.fiscalnorth.auth;

import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import de.fiscalnorth.user.repository.UserRepository;
import de.fiscalnorth.user.service.UserOnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuth2UserProvisioningService {

    private final UserRepository userRepository;
    private final UserOnboardingService userOnboardingService;

    @Transactional
    public User provisionFromOidc(OidcUser oidcUser) {
        String googleSubjectId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String picture = oidcUser.getPicture();

        User user = userRepository.findByGoogleSubjectId(googleSubjectId)
                .orElseGet(() -> userRepository.findByEmail(email).orElse(null));

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUserName(name != null ? name : email.split("@")[0]);
            user.setGoogleSubjectId(googleSubjectId);
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setAvatarUrl(picture);
            user.setUserRole(UserRole.User);
            user = userRepository.save(user);
            userOnboardingService.seedDefaultCategories(user);
            return user;
        }

        if (user.getGoogleSubjectId() == null) {
            user.setGoogleSubjectId(googleSubjectId);
            user.setAuthProvider(user.getAuthProvider() == AuthProvider.LOCAL
                    ? AuthProvider.BOTH
                    : user.getAuthProvider());
        }
        if (picture != null) {
            user.setAvatarUrl(picture);
        }
        if (name != null && (user.getUserName() == null || user.getUserName().isBlank())) {
            user.setUserName(name);
        }
        return userRepository.save(user);
    }
}
