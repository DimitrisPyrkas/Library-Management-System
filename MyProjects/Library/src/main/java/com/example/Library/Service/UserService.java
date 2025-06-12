package com.example.Library.Service;

import com.example.Library.Entity.User;
import com.example.Library.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

//Service for managing user-related operations
//Handles saving,retrieving and deleting users

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));//encodes user's password before saving
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
