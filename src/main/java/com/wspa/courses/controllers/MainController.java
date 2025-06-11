package com.wspa.courses.controllers;

import com.wspa.courses.dtos.LoginForm;
import com.wspa.courses.dtos.UserRegistration;
import com.wspa.courses.entities.*;
import com.wspa.courses.services.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final UserService        userService;
    private final CourseService      courseService;
    private final EnrollmentService enrollmentService;

    private final MaterialService materialService;

    public MainController(UserService userService,
                          CourseService courseService,
                          EnrollmentService enrollmentService,
                          MaterialService materialService) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.materialService = materialService;
    }

    /* ---------- helpers ---------- */

    private Optional<Users> currentUser(HttpServletRequest req) {
        if (req.getCookies() == null) return Optional.empty();
        for (Cookie c : req.getCookies()) {
            if ("username".equals(c.getName()))
                return userService.findByUsername(c.getValue());
        }
        return Optional.empty();
    }

    /* ---------- main page ---------- */

    @GetMapping("/")
    public String mainPage(@RequestParam Optional<String> level,
                           @RequestParam Optional<String> duration,
                           HttpServletRequest request,
                           Model model) {


        currentUser(request).ifPresentOrElse(u -> {
            model.addAttribute("currentUser", u);
            List<Enrollment> list = enrollmentService.forUser(u);
            model.addAttribute("enrollments", list);

            // ➊  карта courseId → Enrollment
            Map<Long, Enrollment> enrollMap = list.stream()
                    .collect(Collectors.toMap(
                            e -> e.getCourse().getId(),
                            e -> e));
            model.addAttribute("enrollMap", enrollMap);
        },()->{

            model.addAttribute("enrollMap", Collections.emptyMap());
        });

        // filters back to page
        model.addAttribute("selectedLevel",    level.orElse(""));
        model.addAttribute("selectedDuration", duration.orElse(""));

        // catalog
        model.addAttribute("courses",
                courseService.getFiltered(level, duration));

        // user-specific blocks
        currentUser(request).ifPresent(u -> {
            model.addAttribute("currentUser", u);
            model.addAttribute("enrollments", enrollmentService.forUser(u));
        });

        // empty forms (for modal / sections)
        if (!model.containsAttribute("loginForm"))
            model.addAttribute("loginForm", new LoginForm());
        if (!model.containsAttribute("registrationForm"))
            model.addAttribute("registrationForm", new UserRegistration());

        return "main";
    }

    /* ---------- login ---------- */

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult br,
                        RedirectAttributes ra,
                        HttpServletResponse resp) {

        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.loginForm", br);
            ra.addFlashAttribute("loginForm", form);
            return "redirect:/#login";
        }

        if (userService.authenticate(form.getUsername(), form.getPassword())) {
            Cookie ck = new Cookie("username", form.getUsername());
            ck.setHttpOnly(true);
            ck.setPath("/");
            ck.setMaxAge(60*60*24);
            resp.addCookie(ck);
            return "redirect:/";
        }

        ra.addFlashAttribute("loginError", "Invalid username or password");
        return "redirect:/#login";
    }

    /* ---------- logout ---------- */
    @PostMapping("/logout")
    public String logout(HttpServletResponse resp) {
        Cookie ck = new Cookie("username", "");
        ck.setMaxAge(0);
        ck.setPath("/");
        resp.addCookie(ck);
        return "redirect:/";
    }

    /* ---------- registration ---------- */

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") UserRegistration form,
                           BindingResult br,
                           RedirectAttributes ra,
                           HttpServletResponse resp) {

        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", br);
            ra.addFlashAttribute("registrationForm", form);
            return "redirect:/#register";
        }

        if (userService.registerUser(form)) {
            ra.addFlashAttribute("successMessage", "Registration successful!");
            Cookie ck = new Cookie("username", form.getUsername());
            ck.setHttpOnly(true);
            ck.setPath("/");
            ck.setMaxAge(60*60*24);
            resp.addCookie(ck);
            return "redirect:/#login";
        }

        ra.addFlashAttribute("registrationError",
                "Username or e-mail already exists");
        return "redirect:/#register";
    }

    /* ---------- enroll action (user clicks “Enroll Now”) ---------- */
    @PostMapping("/enroll/{courseId}")
    public String enroll(@PathVariable Long courseId,
                         HttpServletRequest req,
                         RedirectAttributes ra) {

        Optional<Users> userOpt = currentUser(req);
        if (userOpt.isEmpty()) {
            ra.addFlashAttribute("loginError", "Please log in first");
            return "redirect:/#login";
        }
        userService.enrollUserToCourse(userOpt.get(), courseId);  // simple wrapper
        return "redirect:/#dashboard";
    }

    @GetMapping("/courses/{id}")
    public String showCourse(@PathVariable Long id,
                             HttpServletRequest req,
                             Model model) {

        Course course = courseService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Material> materials = materialService.forCourse(id);

        // группируем по типу для удобного вывода
        Map<MaterialType, List<Material>> byType = materials.stream()
                .collect(Collectors.groupingBy(Material::getMaterialType));

        model.addAttribute("course", course);
        model.addAttribute("videos", byType.getOrDefault(MaterialType.VIDEO, List.of()));
        model.addAttribute("pdfs",    byType.getOrDefault(MaterialType.PDF,   List.of()));
        model.addAttribute("links",   byType.getOrDefault(MaterialType.LINK,  List.of()));

        currentUser(req).ifPresent(u -> model.addAttribute("currentUser", u));
        return "course";
    }
    @PostMapping("/disenroll/{courseId}")
    public String disenroll(@PathVariable Long courseId,
                           HttpServletRequest req,
                           RedirectAttributes ra) {

        Optional<Users> userOpt = currentUser(req);
        if (userOpt.isEmpty()) {
            ra.addFlashAttribute("loginError", "Please log in first");
            return "redirect:/#login";
        }
        userService.disenrollUserFromCourse(userOpt.get(), courseId);
        return "redirect:/#dashboard";
    }
}