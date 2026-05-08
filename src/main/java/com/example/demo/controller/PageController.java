package com.example.demo.controller;

<<<<<<< HEAD
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
=======
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
>>>>>>> fix-profile
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
<<<<<<< HEAD
import java.io.IOException;
=======
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
>>>>>>> fix-profile

@Controller
public class PageController {

    @Autowired
    private UserRepository repo;

<<<<<<< HEAD
    // REGISTER PAGE
=======
    /* =========================
       REGISTER
    ========================= */
>>>>>>> fix-profile
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

<<<<<<< HEAD
    // HANDLE REGISTER
=======
>>>>>>> fix-profile
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {

        if (repo.findByUsername(user.getUsername()).isPresent()) {
            return "redirect:/register?error=exists";
        }

<<<<<<< HEAD
=======
        // ✅ calculate REAL password strength BEFORE encoding
        int strength = calculatePasswordStrength(user.getPassword());
        user.setPasswordStrength(strength);

>>>>>>> fix-profile
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        user.setRole("USER");

        repo.save(user);

        return "redirect:/login";
    }

<<<<<<< HEAD
    // HOME PAGE ✅ INSIDE CLASS
=======
    /* =========================
       HOME & LOGIN
    ========================= */
>>>>>>> fix-profile
    @GetMapping("/")
    public String home() {
        return "home";
    }

<<<<<<< HEAD
    // LOGIN PAGE
=======
>>>>>>> fix-profile
    @GetMapping("/login")
    public String login() {
        return "login";
    }

<<<<<<< HEAD
    // PROFILE PAGE
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        String username = auth.getName();
        User user = repo.findByUsername(username).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }

    // UPDATE PROFILE
    @PostMapping("/update-profile")
    public String updateProfile(
            String name,
            String gender,
            MultipartFile image,
            Authentication auth) throws IOException {
=======
    /* =========================
       PROFILE
    ========================= */
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
>>>>>>> fix-profile

        String username = auth.getName();
        User user = repo.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

<<<<<<< HEAD
        user.setName(name);
        user.setGender(gender);

        if (!image.isEmpty()) {
            String contentType = image.getContentType();

            if (!contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
                return "redirect:/profile?error=invalidfile";
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String uploadDir = "src/main/resources/static/uploads/";

            File file = new File(uploadDir + fileName);
            image.transferTo(file);

            user.setProfileImagePath("/uploads/" + fileName);
        }

        repo.save(user);

        return "redirect:/profile";
=======
        model.addAttribute("user", user);

        int score = calculateSecurityScore(user);
        model.addAttribute("securityScore", score);

        model.addAttribute("device", "Desktop");

        String level;

        if (score < 40) level = "Low";
        else if (score < 70) level = "Medium";
        else level = "High";

        model.addAttribute("securityLevel", level);

        return "profile";
    }

    /* =========================
       PASSWORD STRENGTH
    ========================= */
    private int calculatePasswordStrength(String password) {
        int score = 0;

        if (password.length() >= 6) score += 20;
        if (password.length() >= 10) score += 20;
        if (password.matches(".*[A-Z].*")) score += 20;
        if (password.matches(".*[0-9].*")) score += 20;
        if (password.matches(".*[^A-Za-z0-9].*")) score += 20;

        return score;
    }

    /* =========================
       SECURITY SCORE (FIXED)
    ========================= */
    private int calculateSecurityScore(User user) {
        int score = 0;

        // ✅ use stored password strength (NOT encoded password)
        score += user.getPasswordStrength();

        if (user.getName() != null && !user.getName().isEmpty()) {
            score += 15;
        }

        if (user.getGender() != null && !user.getGender().isEmpty()) {
            score += 15;
        }

        if (user.getProfileImagePath() != null) {
            score += 20;
        }

        if (user.getUsername() != null) {
            score += 10;
        }

        // bonus
        score += 15;

        return Math.min(score, 100);
    }

    /* =========================
       UPDATE PROFILE
    ========================= */
    @PostMapping("/update-profile")
    public String updateProfile(
            @RequestParam("name") String name,
            @RequestParam("gender") String gender,
            @RequestParam("image") MultipartFile file,
            Principal principal) {

        try {
            User user = repo.findByUsername(principal.getName()).orElse(null);

            if (user == null) {
                return "redirect:/login";
            }

            user.setName(name);
            user.setGender(gender);

            if (file != null && !file.isEmpty()) {

                String uploadDir = "uploads/";
                File dir = new File(uploadDir);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, file.getBytes());

                user.setProfileImagePath("/uploads/" + fileName);
            }

            repo.save(user);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/profile?error";
        }

        return "redirect:/profile?success";
>>>>>>> fix-profile
    }
}