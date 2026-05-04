package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(UserRepository repo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

 if (repo.findByUsername("admin").isEmpty()) {

    User user = new User();
    user.setUsername("admin");
    user.setPassword(encoder.encode("1234"));
    user.setName("Admin User");
    user.setGender("Female");

    repo.save(user);
}
        };
    }
}