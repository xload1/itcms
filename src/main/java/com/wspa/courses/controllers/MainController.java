package com.wspa.courses.controllers;

import com.wspa.courses.dtos.LoginForm;
import com.wspa.courses.dtos.UserRegistration;
import com.wspa.courses.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    // GET / => Renders main page with empty forms if none exist
    @GetMapping("/")
    public String main(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new UserRegistration());
        }
        return "main"; // Your Thymeleaf template
    }

    // POST /login => Process login
    @PostMapping("/login")
    public String processLogin(
            @Valid @ModelAttribute("loginForm") LoginForm loginForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Put errors & form data into flash attributes
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginForm", bindingResult);
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            // Return to main page, jumping to #login
            return "redirect:/#login";
        }

        boolean isAuthenticated = userService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        if (isAuthenticated) {
            // On success, you can redirect to some dashboard or reload main
            return "redirect:/";
        } else {
            // Add error message for invalid credentials
            redirectAttributes.addFlashAttribute("loginError", "Invalid username or password.");
            return "redirect:/#login";
        }
    }

    // POST /register => Process registration
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registrationForm") UserRegistration registrationForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Put errors & form data into flash attributes
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", bindingResult);
            redirectAttributes.addFlashAttribute("registrationForm", registrationForm);
            // Return to main, jump to #register
            return "redirect:/#register";
        }

        boolean isRegistered = userService.registerUser(registrationForm);
        if (isRegistered) {
            // Registration successful
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please login.");
            return "redirect:/#login";
        } else {
            // The user or email already exists
            redirectAttributes.addFlashAttribute("registrationError", "A user with that username or email already exists.");
            return "redirect:/#register";
        }
    }
}
