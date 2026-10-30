package org.limir.hotelservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Property View API",
        version = "1.0",
        description = "RESTful API for working with hotels"
))
public class OpenApiConfig {
}
