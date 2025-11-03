package com.srms.srms_app.config;

// **Necessary Imports for the Interface**
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
// **This is the interface we need to implement**
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
// **Ensure this 'implements' part exists**
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Redirect based on the user's role
        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();

            if (authority.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (authority.equals("ROLE_TEACHER")) {
                response.sendRedirect("/teacher/dashboard");
                return;
            } else if (authority.equals("ROLE_FA")) {
                response.sendRedirect("/fa/dashboard");
                return;
            } else if (authority.equals("ROLE_STUDENT")) {
                response.sendRedirect("/student/dashboard");
                return;
            }
        }
        // Fallback
        response.sendRedirect("/");
    }
}