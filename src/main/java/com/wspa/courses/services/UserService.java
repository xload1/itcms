package com.wspa.courses.services;

import com.wspa.courses.entities.Course;
import com.wspa.courses.entities.Users;
import com.wspa.courses.dtos.UserRegistration;
import com.wspa.courses.repos.CourseRepository;
import com.wspa.courses.repos.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EnrollmentService enrollmentService;

    // This method does a simple match check. In production, I'd hash passwords!
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
    public Optional<Users> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public void enrollUserToCourse(Users user, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        enrollmentService.enrollIfAbsent(user, course);
    }
}
