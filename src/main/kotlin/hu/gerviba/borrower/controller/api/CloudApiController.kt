package hu.gerviba.borrower.controller.api

import hu.gerviba.borrower.service.CachedAttributesService
import hu.gerviba.borrower.service.PersistedConfigService
import hu.gerviba.borrower.service.TimeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/private")
class CloudApiController(
    private val persistedConfigService: PersistedConfigService,
    private val cachedAttributesService: CachedAttributesService,
    private val timeService: TimeService
) {

    private val startupTime = timeService.now()

    @PostMapping("/trigger/invalidate-caches")
    fun invalidateCaches(): String {
        persistedConfigService.invalidateCaches()
        cachedAttributesService.invalidateCaches()
        return "ok"
    }

    data class StatsResponseDto(
        val maxMemory: Long,
        val freeMemory: Long,
        val uptime: Long
    )

    @GetMapping("/stats")
    fun stats() = StatsResponseDto(
        maxMemory = Runtime.getRuntime().maxMemory(),
        freeMemory = Runtime.getRuntime().freeMemory(),
        uptime = timeService.now() - startupTime
    )

}
