package hu.gerviba.borrower.service

import org.springframework.stereotype.Service

@Service
class PersistedConfigService {

    var appName: String = "AppName"
    var warningMessage: String = ""
    var warningType: String = "warning"

    fun invalidateCaches() {

    }
}
