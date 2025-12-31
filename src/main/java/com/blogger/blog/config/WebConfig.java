package com.blogger.blog.config;

import com.blogger.blog.repositories.AppUserRepository;
import com.blogger.blog.repositories.PrivilegeRepository;
import com.blogger.blog.repositories.RoleRepository;
import com.blogger.blog.security.CustomAccessDeniedHandler;
import com.blogger.blog.security.CustomLoginSuccessHandler;
import com.blogger.blog.security.CustomUserDetailsService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebConfig {

    //private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";
    @Autowired
    private AppUserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PrivilegeRepository privilegeRepository;
    @Autowired private CustomLoginSuccessHandler loginSuccessHandler;
    @Autowired private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://blog-frontend-kv4q.onrender.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    //    @Bean
//    public WebMvcConfigurer webMvcConfigurer() {
//        return new WebMvcConfigurer() {
//
//            // CORS configuration
////            @Override
////            public void addCorsMappings(CorsRegistry registry) {
////                registry.addMapping("/api/**")
////                        .allowedOrigins("http://localhost:3000")
////                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
////                        .allowedHeaders("*")
////                        .allowCredentials(true);
////            }
//
//            @Bean
//            public CorsConfigurationSource corsConfigurationSource() {
//                CorsConfiguration config = new CorsConfiguration();
//                config.setAllowedOrigins(List.of(
//                        "http://localhost:3000",
//                        System.getenv("https://blog-frontend-kv4q.onrender.com")
//                ));
//                config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
//                config.setAllowedHeaders(List.of("*"));
//                config.setAllowCredentials(true);
//
//                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//                source.registerCorsConfiguration("/**", config);
//                return source;
//            }
//            // Serve uploaded files
////            @Override
////            public void addResourceHandlers(ResourceHandlerRegistry registry) {
////                registry.addResourceHandler("/uploads/**")
////                        .addResourceLocations("file:" + UPLOAD_DIR);
////            }
//        };
//    }
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("200MB"));
        factory.setMaxRequestSize(DataSize.parse("200MB"));
        return factory.createMultipartConfig();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository, roleRepository, privilegeRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ✅ Main Security Config
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/posts/blog/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().permitAll() // 👈 TEMPORARY to confirm fix
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }


    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET"),
                "secure", true
        ));
    }
}
