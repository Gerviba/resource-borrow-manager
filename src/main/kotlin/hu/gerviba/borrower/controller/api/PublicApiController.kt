package hu.gerviba.borrower.controller.api

import hu.gerviba.borrower.service.TimeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PublicApiController(
    private val timeService: TimeService
) {

    @GetMapping("/time")
    fun time(): String {
        return timeService.now().toString()
    }

    @GetMapping("/health")
    fun health(): String {
        return "ok"
    }

    @GetMapping("/version")
    fun version(): String {
        return javaClass.getPackage()?.implementationVersion ?: "dev-snapshot"
    }

}
