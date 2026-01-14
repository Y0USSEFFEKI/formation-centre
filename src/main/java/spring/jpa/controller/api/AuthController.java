package spring.jpa.controller.api;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import spring.jpa.dto.AuthRequest;
import spring.jpa.dto.AuthResponse;
import spring.jpa.dto.RegisterRequest;
import spring.jpa.entity.Etudiant;
import spring.jpa.entity.Formateur;
import spring.jpa.entity.User;
import spring.jpa.repository.EtudiantRepository;
import spring.jpa.repository.FormateurRepository;
import spring.jpa.repository.UserRepository;
import spring.jpa.security.CustomUserDetailsService;
import spring.jpa.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final FormateurRepository formateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager,
                          CustomUserDetailsService userDetailsService,
                          JwtService jwtService,
                          UserRepository userRepository,
                          EtudiantRepository etudiantRepository,
                          FormateurRepository formateurRepository,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.etudiantRepository = etudiantRepository;
        this.formateurRepository = formateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
        Set<String> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            if (etudiantRepository.findByUser_Username(user.getUsername()).isPresent()) {
                roles.add("ROLE_ETUDIANT");
            }
            if (formateurRepository.findByUser_Username(user.getUsername()).isPresent()) {
                roles.add("ROLE_FORMATEUR");
            }
            if (!roles.isEmpty()) {
                user.setRoles(roles);
                userRepository.save(user);
            }
        }
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setRoles(roles);
        resp.setFullname(user.getFullname());
        return resp;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFullname(req.getPrenom() + " " + req.getNom());
        user.setRoles(Set.of("ROLE_" + req.getRole()));

        user = userRepository.save(user);

        if ("ETUDIANT".equalsIgnoreCase(req.getRole())) {
            Etudiant e = new Etudiant();
            e.setMatricule(req.getUsername());
            e.setPrenom(req.getPrenom());
            e.setNom(req.getNom());
            e.setEmail(req.getEmail());
            e.setUser(user);
            etudiantRepository.save(e);
        } else if ("FORMATEUR".equalsIgnoreCase(req.getRole())) {
            Formateur f = new Formateur();
            f.setPrenom(req.getPrenom());
            f.setNom(req.getNom());
            f.setEmail(req.getEmail());
            f.setUser(user);
            formateurRepository.save(f);
        }
    }
}
