package hu.gerviba.borrower.controller

import hu.gerviba.borrower.service.SearchService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class MainController(
    private val searchService: SearchService
) {

    @GetMapping("")
    fun index(): String {
        return "index"
    }

    @GetMapping("/search")
    fun searchForResources(model: Model, @RequestParam(defaultValue = "") q: String): String {
        val search = searchService.search(q)
        println(search)
        model.addAttribute("query", q)
        model.addAttribute("results", search)
        return "search"
    }

    @GetMapping("/resource/{resourceId}")
    fun showResource(@PathVariable resourceId: Int) {

    }

    @GetMapping("/resource/{resourceId}/request")
    fun requestResourceFormTarget(@PathVariable resourceId: Int) {

    }

    @GetMapping("/groups")
    fun showGroups(): String {
        return "groups"
    }

}
