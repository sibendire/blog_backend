package com.blogger.blog.security;

import com.blogger.blog.entities.AppUser;
import com.blogger.blog.repositories.AppUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AppUserRepository userRepository;

    public CustomLoginSuccessHandler(AppUserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String requestUri = request.getRequestURI();

        // Store the user in session
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        AppUser appUser = userRepository.findByEmail(email);
        request.getSession().setAttribute("loggedInUser", appUser);

        // Always redirect to dashboard if needed
        setDefaultTargetUrl("/");
        System.out.println("Redirecting to the admin dashboard");

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
