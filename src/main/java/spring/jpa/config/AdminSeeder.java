package spring.jpa.config;

import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.jpa.entity.User;
import spring.jpa.repository.UserRepository;

@Configuration
public class AdminSeeder {

  @Bean
  CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
    return args -> {
      String username = "admin";
      if (users.findByUsername(username).isPresent()) {
        return;
      }

      User admin = new User();
      admin.setUsername(username);
      admin.setPassword(encoder.encode("admin123"));
      admin.setFullname("Admin");
      admin.setRoles(Set.of("ROLE_ADMIN"));
      users.save(admin);
    };
  }
}
