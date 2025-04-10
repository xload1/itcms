package com.wspa.courses.services;

import com.wspa.courses.Entities.Users;
import com.wspa.courses.dtos.UserRegistration;
import com.wspa.courses.repos.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;

    // This method does a simple match check. In production, you'd hash passwords!
    public boolean authenticate(String username, String password) {
        return usersRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    public boolean registerUser(UserRegistration registrationForm) {
        // Check if the user already exists by username or email
        if (usersRepository.existsByEmail(registrationForm.getEmail()) ||
                usersRepository.existsByUsername(registrationForm.getUsername())) {
            return false; // Duplicate user
        }

        // Create and save a new user
        Users newUser = new Users();
        newUser.setUsername(registrationForm.getUsername());
        newUser.setLastName(registrationForm.getLastName());
        newUser.setEmail(registrationForm.getEmail());
        newUser.setPassword(registrationForm.getPassword()); // Should hash before saving
        newUser.setRole(registrationForm.getRole()); // Use the user-submitted role
        usersRepository.save(newUser);

        return true;
    }
}
