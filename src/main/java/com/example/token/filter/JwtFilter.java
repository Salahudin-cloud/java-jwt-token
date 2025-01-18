package com.example.token.filter;

import com.example.token.model.CustomUserDetails;
import com.example.token.services.CustomUserDetailsService;
import com.example.token.services.JWTServices;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter  extends OncePerRequestFilter {

    @Autowired
    private JWTServices jwtServices;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extract token from the header
            try {
                username = jwtServices.extractUsername(token); // Extract username from token

                // If username is not null and token is valid, authenticate the user
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);

                    if (jwtServices.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (SignatureException e) {
                // Token signature is invalid
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature");
                return;
            } catch (ExpiredJwtException e) {
                // Token is expired
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;
            } catch (MalformedJwtException e) {
                // Token is malformed
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed token");
                return;
            } catch (JwtException e) {
                // General JWT exception (for other cases)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            } catch (Exception e) {
                // Catch any other exceptions
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token is not valid");
                return;
            }
        }

        // Continue the filter chain if token is valid or not present
        filterChain.doFilter(request, response);
    }

}