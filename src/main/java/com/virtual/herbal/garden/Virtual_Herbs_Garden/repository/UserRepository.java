package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Role;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByBannedTrue();
    List<User> findAllByRole(Role role);
}
