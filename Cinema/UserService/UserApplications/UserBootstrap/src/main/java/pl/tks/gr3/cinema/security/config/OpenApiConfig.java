package pl.tks.gr3.cinema.security.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme scheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");

        return new OpenAPI()
                .info(new Info().title("User Service OpenAPI").version("1.0.0").description("Open API documentation for user service"))
                .schemaRequirement("Authorization", scheme)
                .servers(List.of(new Server().url("/user").description("URL to OpenAPI documentation for user service.")))
                .security(List.of(new SecurityRequirement().addList("Authorization")))
                .path("/actuator/health/readiness",
                        new PathItem().get(new Operation().summary("Readiness check")
                                        .responses(new ApiResponses()
                                                .addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse().description("Readiness check was successful"))
                                                .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), new ApiResponse().description("Readiness check was not successful")))
                                .addSecurityItem(new SecurityRequirement().addList("Authorization"))))
                .path("/actuator/health/liveness",
                        new PathItem().get(new Operation().summary("Liveness check")
                                        .responses(new ApiResponses()
                                                .addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse().description("Liveness check was successful"))
                                                .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), new ApiResponse().description("Liveness check was not successful")))
                                .addSecurityItem(new SecurityRequirement().addList("Authorization"))));
    }
}
