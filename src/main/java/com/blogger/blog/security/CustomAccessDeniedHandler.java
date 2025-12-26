package com.blogger.blog.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        String user = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous";
        System.out.println("ACCESS DENIED: " + user + " tried to access " + request.getRequestURI());
        response.sendRedirect("/access-denied");
    }
}
