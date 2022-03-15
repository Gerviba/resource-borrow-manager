package hu.gerviba.borrower.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class MvcConfig(
    @Value("\${app.external:/etc/app/external/}")
    private val uploadPath: String
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/cdn/**")
            .addResourceLocations("file:$uploadPath")
    }

}
