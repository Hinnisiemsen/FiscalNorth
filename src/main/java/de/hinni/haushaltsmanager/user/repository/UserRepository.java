package de.hinni.haushaltsmanager.user.repository;

import de.hinni.haushaltsmanager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
