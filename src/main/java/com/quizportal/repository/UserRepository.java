package com.quizportal.repository;

import com.quizportal.entity.User;
import com.quizportal.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDisplayId(String displayId);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    List<User> findByRole(Role role);

    List<User> findByRoleAndNameContainingIgnoreCase(Role role, String keyword);

}