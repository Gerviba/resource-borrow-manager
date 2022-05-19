package hu.gerviba.borrower.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "hu.borrower.config")
class AppConfig(
    var generatedIdLength: Int = 20,
    var testingMode: Boolean = false,
    var qrGenerationTarget: String = "/etc/app/external/codes",
    var uploadPath: String = "/etc/app/external",
    var qrSize: Int = 360,
    var appBaseUrl: String = "http://127.0.0.1:8080",
    var userInfoUri: String = "https://auth.sch.bme.hu/api/profile"
) {

    override fun toString(): String {
        return "AppConfig(generatedIdLength=$generatedIdLength, testingMode=$testingMode, qrGenerationTarget='$qrGenerationTarget', uploadPath='$uploadPath', qrSize=$qrSize, appBaseUrl='$appBaseUrl', userInfoUri='$userInfoUri')"
    }
}
