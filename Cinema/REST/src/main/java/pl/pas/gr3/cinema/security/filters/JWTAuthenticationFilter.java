package pl.pas.gr3.cinema.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.pas.gr3.cinema.common.dto.output.ExceptionOutputDTO;
import pl.pas.gr3.cinema.utils.I18n;
import pl.pas.gr3.cinema.utils.providers.JWTService;
import pl.pas.gr3.cinema.utils.providers.SecurityConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        ObjectMapper objectMapper = new ObjectMapper();

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            String jwtToken = authHeader.replaceAll("\\s+", "").substring(6);
            String userName = jwtService.extractUsername(jwtToken);

            if (!jwtService.isTokenValid(jwtToken) || userName == null ||
                    userName.isBlank() || userDetailsService.loadUserByUsername(userName) == null) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write(objectMapper.writeValueAsString(
                        new ExceptionOutputDTO(I18n.APPLICATION_INVALID_ACCESS_TOKEN_EXCEPTION)));
                SecurityContextHolder.clearContext();
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                DecodedJWT decodedJWT = JWT.decode(jwtToken);
                List<String> jwtRoles = decodedJWT.getClaim(SecurityConstants.ACCESS_LEVELS).asList(String.class);
                List<GrantedAuthority> authorities = new ArrayList<>();

                for (String role : jwtRoles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
                authorities.add(new SimpleGrantedAuthority("ROLE_AUTHENTICATED"));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
