package de.fiscalnorth.config;

import de.fiscalnorth.auth.AppUserPrincipal;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2PrincipalBridgeFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof OidcUser oidcUser
                && !(authentication.getPrincipal() instanceof AppUserPrincipal)) {
            String googleSubjectId = oidcUser.getSubject();
            userRepository.findByGoogleSubjectId(googleSubjectId).ifPresent(user -> {
                AppUserPrincipal principal = new AppUserPrincipal(user);
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(token);
            });
        }
        filterChain.doFilter(request, response);
    }
}
