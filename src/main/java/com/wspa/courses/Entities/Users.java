package com.wspa.courses.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username used for login
    @Column(nullable = false, unique = true)
    private String username;

    // Last name field
    @Column(nullable = false)
    private String lastName;

    // Email address
    @Column(nullable = false, unique = true)
    private String email;

    // Encrypted password
    @Column(nullable = false)
    private String password;

    // User role, for example: "STUDENT", "INSTRUCTOR", "ADMIN"
    @Column(nullable = false)
    private String role;

    // Timestamp for when the record was created
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
