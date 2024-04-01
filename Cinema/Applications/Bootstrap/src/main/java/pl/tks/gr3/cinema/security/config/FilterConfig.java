package pl.tks.gr3.cinema.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.tks.gr3.cinema.application_services.services.JWSService;
import pl.tks.gr3.cinema.security.filters.ObjectUpdateFilter;
import pl.tks.gr3.cinema.security.filters.UserUpdateFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JWSService jwsService;

    @Bean
    public FilterRegistrationBean<UserUpdateFilter> userUpdateFilter() {
        FilterRegistrationBean<UserUpdateFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserUpdateFilter(jwsService));
        registrationBean.setUrlPatterns(List.of("/api/v1/admins/update", "/api/v1/clients/update", "/api/v1/staffs/update"));
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ObjectUpdateFilter> objectUpdateFilter() {
        FilterRegistrationBean<ObjectUpdateFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ObjectUpdateFilter());
        registrationBean.setUrlPatterns(List.of("/api/v1/movies/update", "/api/v1/tickets/update"));
        return registrationBean;
    }
}
