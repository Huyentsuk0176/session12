package com.example.session12.config;

import com.example.session12.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ❌ CSRF (fix cho H2 + API)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable()
                )

                // 🔐 PHÂN QUYỀN
                .authorizeHttpRequests(auth -> auth

                        // 👀 ai cũng xem được sản phẩm
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // 🔐 chỉ ADMIN + STAFF được thêm/sửa/xoá
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("ADMIN", "STAFF")

                        // auth + h2
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/h2-console/**").permitAll()

                        // còn lại phải login
                        .anyRequest().authenticated()
                )

                // ❌ disable form login (vì dùng JWT)
                .formLogin(form -> form.disable())

                // ❌ disable session (REST chuẩn)
                .httpBasic(Customizer.withDefaults())

                // 👇 fix H2 console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        // 🔥 JWT filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔑 mã hoá password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}