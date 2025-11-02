package com.intellisoft.digitalhealthbackend.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.Column;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Digital Health Backend",
                version = "1.0.0",
                description = "Api for managing patients, their encounters and observations",
                contact = @Contact(
                        name = "Felix Maina",
                        email = "karani.maina2010@gmail.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development"),
        }
)
public class ApiConfigurations {

}
