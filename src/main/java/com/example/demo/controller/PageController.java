package com.example.demo.controller;

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

    // HOME PAGE
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // PROFILE PAGE
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {

        String username = auth.getName();
        User user = repo.findByUsername(username).orElse(null);

        model.addAttribute("user", user);

        return "profile";
    }

    // UPDATE PROFILE (SECURE VERSION)
    @PostMapping("/update-profile")
    public String updateProfile(
            String name,
            String gender,
            MultipartFile image,
            Authentication auth) throws IOException {

        String username = auth.getName();
        User user = repo.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        user.setName(name);
        user.setGender(gender);

        // ✅ Secure file upload
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
    }
}