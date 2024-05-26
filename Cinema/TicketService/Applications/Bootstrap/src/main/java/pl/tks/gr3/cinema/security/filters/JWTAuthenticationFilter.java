package pl.tks.gr3.cinema.security.filters;

import com.auth0.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.application_services.services.JWTService;
import pl.tks.gr3.cinema.security.consts.SecurityConstants;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwtToken = authHeader.replaceAll("\\s+", "").substring(6);
        final String userName = jwtService.extractUsername(jwtToken);
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtService.isTokenValid(jwtToken)) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                response.getWriter().write("JWT Token signature is invalid.");
                SecurityContextHolder.clearContext();
                return;
            } else {
                List<SimpleGrantedAuthority> userRoles = List.of(new SimpleGrantedAuthority(JWT.decode(jwtToken).getClaim(UserEntConstants.USER_ROLE).asString()));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(JWT.decode(jwtToken).getSubject(), null, userRoles);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.contains("swagger");
    }
}
