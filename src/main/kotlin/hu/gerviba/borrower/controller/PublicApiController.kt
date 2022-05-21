package hu.gerviba.borrower.controller

import hu.gerviba.borrower.service.TimeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat

@RestController
@RequestMapping("/api")
class PublicApiController(
    private val timeService: TimeService
) {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
    }

    data class TimeResponseDto(var timestamp: Long, var formatted: String)

    @GetMapping("/time")
    fun time(): TimeResponseDto {
        val time = timeService.now()
        return TimeResponseDto(time, DATE_FORMAT.format(time))
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
