package hu.gerviba.borrower.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.config")
data class StartupConfiguration(
    val defaultAppName: String = ""
)
