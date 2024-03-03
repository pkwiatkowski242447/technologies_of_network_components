package pl.tks.gr3.cinema.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ObjectUpdateFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String ifMatchHeader = request.getHeader(HttpHeaders.IF_MATCH);
        if (ifMatchHeader == null || ifMatchHeader.isEmpty()) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            httpServletResponse.getWriter().write("If-Match header content is missing.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
