package pl.pas.gr3.cinema.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.pas.gr3.cinema.security.filters.ObjectUpdateFilter;

import java.util.List;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ObjectUpdateFilter> objectUpdateFilter() {
        FilterRegistrationBean<ObjectUpdateFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ObjectUpdateFilter());
        registrationBean.setUrlPatterns(List.of("/api/v1/admins", "/api/v1/clients", "/api/v1/staffs", "/api/v1/movies", "/api/v1/tickets"));
        return registrationBean;
    }
}
