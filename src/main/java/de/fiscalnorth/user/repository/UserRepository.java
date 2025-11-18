package de.fiscalnorth.user.repository;

import de.fiscalnorth.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
