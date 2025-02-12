package com.saas.dental_clinic.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            logger.info("JWT Token: " + jwt);

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                boolean tokenValid = tokenProvider.validateToken(jwt);
                logger.info("Token Validation Result: " + tokenValid);

                if (tokenValid) {
                    String username = tokenProvider.getUsernameFromJWT(jwt);
                    logger.info("Username from Token: " + username);

//
//                UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Extraire les rôles du JWT
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(Keys.hmacShaKeyFor(tokenProvider.getJwtSecret().getBytes()))
                            .build()
                            .parseClaimsJws(jwt)
                            .getBody();

                    // Récupérer les rôles depuis les claims
                    String rolesString = claims.get("roles", String.class);
                    List<String> roles = Arrays.asList(rolesString.split(","));

                    logger.info("Roles from Token: " + roles);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Convertir les rôles en Authorities
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // Créer un objet d'authentification avec les rôles extraits
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Enregistrer l'authentification dans le SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}