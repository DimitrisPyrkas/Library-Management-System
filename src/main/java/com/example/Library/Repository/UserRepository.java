package com.example.Library.Repository;

import com.example.Library.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//UserRepository handles all data access operations related to User Entity

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
