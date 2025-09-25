//package com.blogger.blog.security;
//
//import com.blogger.blog.repositories.AppUserRepository;
//import com.blogger.blog.repositories.PrivilegeRepository;
//import com.blogger.blog.repositories.RoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true) // replaces EnableGlobalMethodSecurity
//public class WebSecurityConfig {
//
//    @Autowired private AppUserRepository userRepository;
//    @Autowired private RoleRepository roleRepository;
//    @Autowired private PrivilegeRepository privilegeRepository;
//    @Autowired private CustomLoginSuccessHandler loginSuccessHandler;
//    @Autowired private CustomAccessDeniedHandler customAccessDeniedHandler;
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new CustomUserDetailsService(userRepository, roleRepository, privilegeRepository);
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService());
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }
//
//    // ✅ Main Security Config
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling(ex -> ex
//                        .accessDeniedHandler(customAccessDeniedHandler)
//                )
//                .formLogin(form -> form
//                        .loginPage("/")
//                        .usernameParameter("email")
//                        .passwordParameter("password")
//                        .successHandler(loginSuccessHandler) // ✅ Use custom handler
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/")
//                        .permitAll()
//                )
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session
//                        .invalidSessionUrl("/?session=invalid")
//                );
//
//        return http.build();
//    }
//}
