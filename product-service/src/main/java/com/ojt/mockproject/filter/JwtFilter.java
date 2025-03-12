package com.ojt.mockproject.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.exceptionhandler.token.ErrorResponseUtil;
import com.ojt.mockproject.exceptionhandler.InvalidToken;
import com.ojt.mockproject.service.AccountService;
import com.ojt.mockproject.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;
    @Autowired
    @Lazy
    private AccountService accountService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token= null;
        String email = null;
        String uri = request.getRequestURI();
        if (uri.contains("/auth/login") || uri.contains("/auth/register")) {
            filterChain.doFilter(request,response);
            return;
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtService.extractEmail(token);
            } catch (TokenExpiredException e) {
                // Handle expired token
                ResponseEntity<String> responseEntity = ErrorResponseUtil.createErrorResponse(HttpStatus.UNAUTHORIZED, "Token has expired");
                response.setStatus(responseEntity.getStatusCodeValue());
                response.getWriter().write(responseEntity.getBody());
                return;
            } catch (InvalidToken e) {
                // Handle invalid token
                ResponseEntity<String> responseEntity = ErrorResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid token");
                response.setStatus(responseEntity.getStatusCodeValue());
                response.getWriter().write(responseEntity.getBody());
                return;
            }
        }

        if(email !=null && SecurityContextHolder.getContext().getAuthentication()==null){
            Account account = accountService.getAccountByEmail(email);
            if(jwtService.validateToken(token, account)){
                UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(account,null, account.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
