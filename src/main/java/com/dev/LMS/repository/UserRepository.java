package com.dev.LMS.repository;

import com.dev.LMS.model.User;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
@Inheritance(strategy = InheritanceType.JOINED)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @SuppressWarnings("null")
    Optional<User> findById(Long id);
}
