package com.wspa.courses.services;

import com.wspa.courses.Entities.Users;
import com.wspa.courses.dtos.UserRegistration;
import com.wspa.courses.repos.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    // Inject the PasswordEncoder (configured as a bean in your security configuration)
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Updated authenticate method uses the PasswordEncoder to compare hashed passwords.
    public boolean authenticate(String username, String password) {
        return usersRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .isPresent();
    }

    // Updated registration method hashes the password and sets a default role.
    public boolean registerUser(UserRegistration registrationForm) {
        // Check if the user already exists by username or email.
        if (usersRepository.existsByEmail(registrationForm.getEmail()) ||
                usersRepository.existsByUsername(registrationForm.getUsername())) {
            return false; // Duplicate user detected.
        }

        // Create a new user and hash the password.
        Users newUser = new Users();
        newUser.setUsername(registrationForm.getUsername());
        newUser.setLastName(registrationForm.getLastName());
        newUser.setEmail(registrationForm.getEmail());
        newUser.setPassword(passwordEncoder.encode(registrationForm.getPassword()));
        // Instead of using the role provided by the user, assign a safe default.
        newUser.setRole("USER");

        // Save the user in the repository.
        usersRepository.save(newUser);
        return true;
    }
}
