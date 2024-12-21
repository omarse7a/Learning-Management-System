package com.dev.LMS.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.dev.LMS.util.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login").permitAll()
                .requestMatchers("/profile").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                .requestMatchers("/users/**").hasRole("ADMIN")
                // .requestMatchers("/admin/**").hasRole("ADMIN")
                // .requestMatchers("/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                // .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/course/{course-name}/create-assignment").hasRole("INSTRUCTOR")
                .requestMatchers("/course/{course-name}/assignments").hasAnyRole("STUDENT", "INSTRUCTOR")
                .requestMatchers("/course/{course-name}/assignment/{assignment_id}/view").hasAnyRole("STUDENT", "INSTRUCTOR")
                .requestMatchers("/course/{course-name}/assignment/{assignment_id}/submit").hasRole("STUDENT")
                .requestMatchers("/course/{course-name}/assignment/{assignment_id}/submission/{submission_id}").hasRole("INSTRUCTOR")
                .requestMatchers("/course/{course-name}/assignment/{assignment_id}/submissions}").hasRole("INSTRUCTOR")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
