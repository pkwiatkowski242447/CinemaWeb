package pl.pas.gr3.cinema.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {
        "classpath:properties/consts.properties",
        "classpath:properties/key.properties",
        "classpath:properties/urls.properties"
})
public class ApplicationConfig {}
