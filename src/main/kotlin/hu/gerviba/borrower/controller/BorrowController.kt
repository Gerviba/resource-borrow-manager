package hu.gerviba.borrower.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BorrowController {

    @GetMapping("")
    fun index(): String {
        return "index"
    }

    @GetMapping("/search")
    fun searchForResource(): String {
        return "index"
    }

    @GetMapping("/resource/{resourceId}")
    fun showResource(@PathVariable resourceId: Int) {

    }

    @GetMapping("/resource/{resourceId}/request")
    fun requestResourceFormTarget(@PathVariable resourceId: Int) {

    }

}
