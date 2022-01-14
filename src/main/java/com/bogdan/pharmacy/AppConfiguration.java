package com.bogdan.pharmacy;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Pharmacy API")
                .version("1.0")
                .description("This is the documentation for the API"));
    }
}
