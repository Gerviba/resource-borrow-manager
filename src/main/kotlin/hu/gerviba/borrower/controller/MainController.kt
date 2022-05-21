package hu.gerviba.borrower.controller

import hu.gerviba.borrower.service.ResourceService
import hu.gerviba.borrower.service.SearchService
import hu.gerviba.borrower.service.UserService
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.persistence.EntityNotFoundException
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest


@Controller
class MainController(
    private val searchService: SearchService,
    private val resourceService: ResourceService,
    private val userService: UserService,
) : ErrorController {

    @GetMapping("")
    fun index(model: Model, authentication: Authentication?): String {
        model.addAttribute("groupMember", false)
        model.addAttribute("requestCount", 0)
        authentication?.let { userService.addDefaultFields(model, authentication) }
        return "index"
    }

    @GetMapping("/login")
    fun login(): String = "redirect:/oauth2/authorization/authsch"

    @GetMapping("/search")
    fun searchForResources(
        model: Model,
        @RequestParam(defaultValue = "") q: String,
        authentication: Authentication?
    ): String {
        val search = searchService.search(q.ifBlank { "*" })
        model.addAttribute("query", q)
        model.addAttribute("results", search)
        model.addAttribute("groupMember", false)
        model.addAttribute("requestCount", 0)
        authentication?.let { userService.addDefaultFields(model, authentication) }
        return "search"
    }

    @GetMapping("/res/{resourceId}")
    fun showResource(model: Model, @PathVariable resourceId: Int, authentication: Authentication?): String {
        return try {
            val resource = resourceService.getResource(resourceId)
            if (!resource.visible)
                throw EntityNotFoundException()

            model.addAttribute("resource", resource)
            model.addAttribute("bookings", resourceService.getBookingsFromNow(resourceId))
            model.addAttribute("groupMember", false)
            model.addAttribute("requestCount", 0)
            authentication?.let { userService.addDefaultFields(model, authentication) }
            "regular/showResource"
        } catch (e: Exception) {
            model.addAttribute("groupMember", false)
            model.addAttribute("requestCount", 0)
            authentication?.let { userService.addDefaultFields(model, authentication) }
            model.addAttribute("errorMessage", "Erőforrás nem található")
            model.addAttribute("errorDescription", "Az erőforrás kódja vagy azonosítója megváltozott, vagy soha nem is létezett.")
            "errorPage"
        }
    }

    @GetMapping("/identify")
    fun identifyResource(model: Model, authentication: Authentication?): String {
        model.addAttribute("groupMember", false)
        model.addAttribute("requestCount", 0)
        authentication?.let { userService.addDefaultFields(model, authentication) }
        return "identify"
    }

    @GetMapping("/identify/{codeBase64}")
    fun identifyResource(@PathVariable codeBase64: String): String {
        val code = resolveCode(codeBase64)

        return resourceService.getResourceByCode(code)
            .map { resource -> "redirect:/res/${resource.id}" }
            .orElse("redirect:/res/0")
    }

    @GetMapping("/code/{code}")
    fun identifyResourceByCode(@PathVariable code: String): String {
        return resourceService.getResourceByCode(code)
            .map { resource -> "redirect:/res/${resource.id}" }
            .orElse("redirect:/res/0")
    }

    @RequestMapping("/error")
    fun handleError(model: Model, request: HttpServletRequest, authentication: Authentication?): String? {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        authentication?.let { userService.addDefaultFields(model, authentication) }

        model.addAttribute("errorMessage", "ERROR $status")
        model.addAttribute("errorDescription", request.getAttribute(RequestDispatcher.ERROR_MESSAGE))

        if (status != null) {
            when (Integer.valueOf(status.toString())) {
                HttpStatus.NOT_FOUND.value() -> {
                    model.addAttribute("errorMessage", "Oldal nem található")
                    model.addAttribute("errorMessage", "Az oldal el lett mozgatva, vagy soha nem is létezett!")
                }
                HttpStatus.FORBIDDEN.value() -> {
                    model.addAttribute("errorMessage", "Hozzáférés Megtagadva")
                    model.addAttribute("errorDescription", "Nincs jogosultságod megtekinteni ezt az oldalt! " +
                            "Ha azt hiszed, hogy ez egy hiba relogolj vagy vedd fel a kapcsolatot egy adminnal!")
                }
                HttpStatus.INTERNAL_SERVER_ERROR.value() -> {
                    model.addAttribute("errorMessage", "Szerver Hiba")
                }
            }
        }

        return "errorPage"
    }

}
