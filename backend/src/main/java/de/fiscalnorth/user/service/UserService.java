package de.fiscalnorth.user.service;

import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.user.dto.CreateUserRequest;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("User", "id", id));
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setUserRole(request.userRole());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RessourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
}
