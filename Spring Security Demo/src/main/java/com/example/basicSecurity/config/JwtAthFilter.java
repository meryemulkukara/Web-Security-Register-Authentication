package com.example.basicSecurity.config;

import com.example.basicSecurity.service.JwtService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtAthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader=request.getHeader(AUTHORIZATION);
        final String username;
        final String jwtToken;

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        jwtToken=authHeader.substring(7); //TODO find token
        username=jwtService.extractUsername(jwtToken); // TODO to be implemented //extract the useremail form JWT token




        //Control is user already authenticate or not with SecurityContextHolder
        //if our user not null
        if(username != null && SecurityContextHolder.getContext().getAuthentication()== null ){
            //this means user not authenticated, not connected yet

            //then find userdetails
            UserDetails userDetails=this.userDetailsService.loadUserByUsername(username);

            //control jwttoken is it userDetails user token or not and user is valid
            if(jwtService.isTokenValid(jwtToken, userDetails)){
                //distpacher Servlet
                UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                //for some more details
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //up to date security context holder
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request , response);
        }

    }
}
