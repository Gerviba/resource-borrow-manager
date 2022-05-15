package hu.gerviba.borrower.controller

import hu.gerviba.borrower.service.ResourceService
import hu.gerviba.borrower.service.SearchService
import hu.gerviba.borrower.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest

@Controller
class MainController(
    private val searchService: SearchService,
    private val resourceService: ResourceService,
    private val userService: UserService,
) {

    @GetMapping("")
    fun index(model: Model, httpServletRequest: HttpServletRequest): String {
        userService.addDefaultFields(model, httpServletRequest)
        return "index"
    }

    @GetMapping("/search")
    fun searchForResources(
        model: Model,
        @RequestParam(defaultValue = "") q: String,
        httpServletRequest: HttpServletRequest
    ): String {
        val search = searchService.search(q.ifBlank { "*" })
        model.addAttribute("query", q)
        model.addAttribute("results", search)
        userService.addDefaultFields(model, httpServletRequest)
        return "search"
    }

    @GetMapping("/resource/{resourceId}")
    fun showResource(model: Model, @PathVariable resourceId: Int, httpServletRequest: HttpServletRequest): String {
        return try {
            val resource = resourceService.getResource(resourceId)
            if (!resource.visible)
                throw EntityNotFoundException()

            model.addAttribute("resource", resource)
            model.addAttribute("bookings", resourceService.getBookingsFromNow(resourceId))
            userService.addDefaultFields(model, httpServletRequest)
            "regular/showResource"
        } catch (e: Exception) {
            userService.addDefaultFields(model, httpServletRequest)
            model.addAttribute("errorMessage", "Erőforrás nem található")
            model.addAttribute("errorDescription", "Az erőforrás kódja vagy azonosítója megváltozott, vagy soha nem is létezett.")
            "errorPage"
        }
    }

    @GetMapping("/identify")
    fun identifyResource(model: Model, httpServletRequest: HttpServletRequest): String {
        userService.addDefaultFields(model, httpServletRequest)
        return "identify"
    }

    @GetMapping("/identify/{codeBase64}")
    fun identifyResource(@PathVariable codeBase64: String): String {
        val code = resolveCode(codeBase64)

        return resourceService.getResourceByCode(code)
            .map { resource -> "redirect:/resource/${resource.id}" }
            .orElse("redirect:/resource/0")
    }

    @GetMapping("/code/{code}")
    fun identifyResourceByCode(@PathVariable code: String): String {
        return resourceService.getResourceByCode(code)
            .map { resource -> "redirect:/resource/${resource.id}" }
            .orElse("redirect:/resource/0")
    }

}
