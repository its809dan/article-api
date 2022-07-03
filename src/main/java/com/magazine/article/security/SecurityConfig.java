package com.magazine.article.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin123")
                .password("{bcrypt}$2a$10$L2XKxPwwNE.u8K.GEQYYN./1bPiBQx0l8cpLtVhX6vSALSvRV9aL.") // LkQH4ibu9X
                .roles(Role.ADMIN.name())
                .build();

        UserDetails user = User.builder()
                .username("user123")
                .password("{bcrypt}$2a$10$DBGodFa1rBrZBsCXvCiTXOP4wmQXqCNUqZ11ib416mTKzmpluKqBK") // oTEn251qS0
                .roles(Role.USER.name())
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .httpBasic().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/articles/stats").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.GET, "/articles").permitAll()
                .antMatchers(HttpMethod.POST, "/articles").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                .antMatchers("/api-docs/**").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST","PUT", "DELETE");
            }
        };
    }
}
