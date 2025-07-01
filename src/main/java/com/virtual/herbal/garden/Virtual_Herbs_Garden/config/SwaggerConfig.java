package com.virtual.herbal.garden.Virtual_Herbs_Garden.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Virtual Herbal Garden API")
                        .version("1.0")
                        .description("API documentation for Virtual Herbal Garden project")
                )
                .tags(Arrays.asList(
                        new Tag().name("Auth API"),
                        new Tag().name("USER API"),
                        new Tag().name("Admin-Controller"),
                        new Tag().name("Admin-Metrics-Controller"),
                        new Tag().name("Plant API"),
                        new Tag().name("Product Marketplace API"),
                        new Tag().name("product-purchase-controller"),
                        new Tag().name("Product Reviews and Ratings API"),
                        new Tag().name("Order API"),
                        new Tag().name("Bookmark Plant API"),
                        new Tag().name("Blog-Controller"),
                        new Tag().name("Blog-Like-Controller"),
                        new Tag().name("Comment-Controller"),
                        new Tag().name("Feedback Form API"),
                        new Tag().name("Mail-Test-Controller"),
                        new Tag().name("TEST API")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ));
    }
}
