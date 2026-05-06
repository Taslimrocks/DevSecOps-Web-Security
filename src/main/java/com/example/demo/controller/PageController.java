package com.example.demo.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class PageController {

    @Autowired
    private UserRepository repo;

    // REGISTER PAGE
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // HANDLE REGISTER
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {

        if (repo.findByUsername(user.getUsername()).isPresent()) {
            return "redirect:/register?error=exists";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        user.setRole("USER");

        repo.save(user);

        return "redirect:/login";
    }

    // HOME PAGE ✅ INSIDE CLASS
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String login() {
        return "login";
    }

        // 🔐 CALCULATE SECURITY SCORE
        @GetMapping("/profile")
        public String profile(Model model, Authentication auth) {

            String username = auth.getName();
            User user = repo.findByUsername(username).orElse(null);

            if (user == null) {
                return "redirect:/login";
            }

            model.addAttribute("user", user);

            // ✅ REQUIRED (missing = 500 error)
            int score = calculateSecurityScore(user);
            model.addAttribute("securityScore", score);

            // ✅ REQUIRED
            model.addAttribute("device", "Desktop");

            return "profile";
        }
    private int calculateSecurityScore(User user) {
        int score = 0;

        // Strong password (basic check)
        if (user.getPassword() != null && user.getPassword().length() > 8) {
            score += 30;
        }

        // Profile completed
        if (user.getName() != null && !user.getName().isEmpty()) {
            score += 20;
        }

        // Gender set
        if (user.getGender() != null && !user.getGender().isEmpty()) {
            score += 20;
        }

        // Profile image uploaded
        if (user.getProfileImagePath() != null) {
            score += 30;
        }

        return Math.min(score, 100);
    }
    // PROFILE PAGE
    @PostMapping("/update-profile")
    public String updateProfile(
            @RequestParam("name") String name,
            @RequestParam("gender") String gender,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication auth) throws IOException {

        String username = auth.getName();
        User user = repo.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // update fields
        user.setName(name);
        user.setGender(gender);

        // ✅ SAFE IMAGE HANDLING
        System.out.println("Image received: " + (image != null ? image.getOriginalFilename() : "NULL"));
        if (image != null && !image.isEmpty()) {

            String contentType = image.getContentType();

            if (!contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
                return "redirect:/profile?error=invalidfile";
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String uploadDir = "src/main/resources/static/uploads/";

            // ✅ CREATE FOLDER IF NOT EXISTS
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File file = new File(uploadDir + fileName);
            image.transferTo(file);

            user.setProfileImagePath("/uploads/" + fileName);
        }

        repo.save(user);

        return "redirect:/profile";
    }
}