package spring.jpa.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import spring.jpa.security.JwtAuthFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain apiChain(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
	    http.securityMatcher("/api/**")
	        .csrf(csrf -> csrf.disable())
	        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/api/auth/**").permitAll()
	            .requestMatchers(HttpMethod.GET, "/api/catalogue/**").permitAll()
	            .requestMatchers("/api/etudiant/**").hasRole("ETUDIANT")
	            .requestMatchers("/api/formateur/**").hasRole("FORMATEUR")
	            .anyRequest().authenticated()
	        )
	        .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class)
	        .cors(Customizer.withDefaults());

	    return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain webChain(HttpSecurity http) throws Exception {
	    http.securityMatcher("/**")
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/login", "/error").permitAll()
	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            .anyRequest().permitAll()
	        )
	        .formLogin(form -> form
	            .defaultSuccessUrl("/admin/etudiants", true)
	        )
	        .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
	        .csrf(csrf -> csrf.disable());

	    return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:4200"
        ));
        cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cfg);
        return source;
    }
}
