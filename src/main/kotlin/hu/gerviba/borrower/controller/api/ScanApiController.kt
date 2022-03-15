package hu.gerviba.borrower.controller.api

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

//@RestController
class ScanApiController {

    @PutMapping("/api/scan-to-lend/{resourceId}")
    fun scanResourceByManagerToBook(@PathVariable resourceId: String) {

    }

    @PutMapping("/api/scan-to-borrow/{resourceId}")
    fun scanResourceByBorrowerToBook(@PathVariable resourceId: String) {

    }

    @PutMapping("/api/scan-to-return/{resourceId}")
    fun scanResourceByManagerToReturn(@PathVariable resourceId: String) {

    }

    @PutMapping("/api/scan-to-check/{resourceId}")
    fun scanResourceToCheckStatus(@PathVariable resourceId: String) {

    }

}
