package com.hosein.jobportal.config;

import com.hosein.jobportal.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final String[] publicUrl = {"/", "/global-search/**", "/register", "/register/**",
            "/webjars/**", "/resources/**", "/assets/**", "/css/**", "/summernote/**", "/js/**",
            "/*.css","/*.js", "/*.js.map", "/fonts**", "/favicon.ico", "/resources/**", "/error"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authProvider());
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(publicUrl).permitAll();
            auth.anyRequest().authenticated();
        });
        http.formLogin(formLogin -> {
            formLogin.loginPage("/login").permitAll().successHandler(customAuthenticationSuccessHandler);
        }).logout(logout -> {
            logout.logoutUrl("/logout").logoutSuccessUrl("/").;
        }).cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
