package com.example.Library.Controller;

import com.example.Library.Dto.ChangePasswordRequest;
import com.example.Library.Entity.User;
import com.example.Library.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Controller for user-related operations such as registration, password change and user listing

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Open to all for registration (no auth needed)
    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    //Retrieving a list of all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    //Allowing an authenticated user change their password
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User user,
                                            @RequestBody ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Incorrect current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);
        return ResponseEntity.ok("Password updated successfully.");
    }
}
