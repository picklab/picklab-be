package picklab.backend.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        "AccessToken",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT"),
                    ),
            ).info(configurationInfo())
            .addSecurityItem(SecurityRequirement().addList("AccessToken"))

    private fun configurationInfo(): Info =
        Info()
            .title("PickLab API")
            .description("PickLab API 명세서")
            .version("v1.0.0")
}
