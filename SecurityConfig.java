package com.srms.srms_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService; // Keep this import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder; // Use interface
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Password Encoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() { // Return interface type
        return new BCryptPasswordEncoder();
    }

    /* NOTE: We do NOT define a UserDetailsService @Bean here
       because CustomUserDetailsService.java has @Service. */

    // --- Explicitly Configure Authentication Manager ---
    // This tells Spring Security exactly how to check passwords
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailService) // Use our custom service
                .passwordEncoder(passwordEncoder); // Use BCrypt for checking
        return authenticationManagerBuilder.build();
    }
    // --- ---

    // SecurityFilterChain Bean - Defines URL access rules and login/logout behavior
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Allow static resources
                .requestMatchers("/", "/login").permitAll()       // Allow landing and login page
                .requestMatchers("/student/**").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/teacher/**").hasAuthority("ROLE_TEACHER")
                .requestMatchers("/fa/**").hasAuthority("ROLE_FA")
                .anyRequest().authenticated() // All other requests need login
            )
            .csrf(csrf -> csrf.disable()) // CSRF disabled for testing (REMOVE LATER IF NEEDED)
            .formLogin(form -> form
                .loginPage("/login")           // Our custom login page URL
                
                // ----- YE LINE UPDATE KI GAYI HAI (THIS LINE IS UPDATED) -----
                .loginProcessingUrl("/api/login")  // URL the form submits to (based on your console logs)
                
                .successHandler(new CustomLoginSuccessHandler()) // Redirect after successful login
                .failureUrl("/login?error=true") // Redirect here on login failure
                .permitAll()                   // Allow access to login processing
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL to trigger logout
                .logoutSuccessUrl("/login?logout=true") // Redirect after logout
                .invalidateHttpSession(true)       // End the session
                .deleteCookies("JSESSIONID")       // Remove session cookie
                .permitAll()                       // Allow access to logout
            );

        return http.build();
    }
}
