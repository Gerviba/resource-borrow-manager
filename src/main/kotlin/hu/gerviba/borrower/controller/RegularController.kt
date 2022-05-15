package hu.gerviba.borrower.controller

import hu.gerviba.borrower.dto.BookingEntityDto
import hu.gerviba.borrower.dto.BorrowRequestDto
import hu.gerviba.borrower.exception.BusinessOperationFailedException
import hu.gerviba.borrower.service.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest

@Controller
class RegularController(
    private val divisionService: DivisionService,
    private val resourceService: ResourceService,
    private val userService: UserService,
    private val groupService: GroupService,
    private val clock: TimeService
) {

    @GetMapping("/division/{divisionId}")
    fun showDivision(@PathVariable divisionId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("division", divisionService.getDivision(divisionId))
        model.addAttribute("resources", resourceService.getAllForDivision(divisionId))
        model.addAttribute("members", userService.getAllFromDivision(divisionId))
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/showDivision"
    }

    @GetMapping("/groups")
    fun showGroups(model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("groups", groupService.getAllVisible())
        userService.addDefaultFields(model, httpServletRequest)
        return "listGroups"
    }

    @GetMapping("/group/{groupId}")
    fun showGroup(@PathVariable groupId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("group", groupService.getGroup(groupId))
        model.addAttribute("divisions", divisionService.listAllOfGroup(groupId))
        model.addAttribute("members", userService.getAllFromGroup(groupId))
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/showGroup"
    }

    @GetMapping("/resource/{resourceId}/request")
    fun requestResource(model: Model, @PathVariable resourceId: Int, httpServletRequest: HttpServletRequest): String {
        val resource = resourceService.getResource(resourceId)
        if (!resource.visible || !resource.available)
            throw BusinessOperationFailedException("Resource not available")

        model.addAttribute("resource", resource)
        model.addAttribute("newRequest", BorrowRequestDto(
            dateStart = clock.now() / 1000,
            dateEnd = clock.now() / 1000
        ))
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/requestResource"
    }

    @PostMapping("/resource/{resourceId}/request")
    fun requestResourceFormTarget(
        model: Model,
        @PathVariable resourceId: Int,
        @ModelAttribute borrowRequest: BorrowRequestDto,
        httpServletRequest: HttpServletRequest
    ): String {
        try {
            val user = userService.getAllUsers()[0] // FIXME: remove mock
            resourceService.borrowResource(resourceId, borrowRequest, user)
            return "redirect:/resource/$resourceId"
        } catch (e: Exception) {
            model.addAttribute("errorMessage", "Hibás beviteli adatok")
            model.addAttribute("errorDescription", "Figyelj a hibajelzésekre!")
            return "errorPage"
        }
    }

    @GetMapping("/resource/{resourceId}/available/{from}/{to}")
    @ResponseBody
    fun checkAvailability(
        @PathVariable resourceId: Int,
        @PathVariable from: Long,
        @PathVariable to: Long
    ): List<BookingEntityDto> {
        val resource = resourceService.getResource(resourceId)
        if (!resource.visible || to < from)
            return listOf()
        return resourceService.getBookingsWithingRange(resourceId, from * 1000, to * 1000)
    }

    @GetMapping("/resource/{resourceId}/history")
    fun requestResourceHistory(
        model: Model,
        @PathVariable resourceId: Int,
        httpServletRequest: HttpServletRequest
    ): String {
        val resource = resourceService.getResource(resourceId)
        if (!resource.visible)
            throw BusinessOperationFailedException("Not found")

        model.addAttribute("resource", resource)
        model.addAttribute("bookings", resourceService.getAcceptedBookings(resourceId))
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/resourceHistory"
    }

    @GetMapping("/receive/confirm/{codeBase64}")
    fun receiveResource(@PathVariable codeBase64: String, httpServletRequest: HttpServletRequest): String {
        val code = resolveCode(codeBase64)

        val user = userService.getAllUsers()[0]
        return resourceService.receiveResource(user, code)
            .map { resource -> "redirect:/resource-received/${resource.id}" }
            .orElse("redirect:/resource-received/0")
    }

    @GetMapping("/resource-received/{resourceId}")
    fun bookingIssued(model: Model, @PathVariable resourceId: Int, httpServletRequest: HttpServletRequest): String {
        return try {
            val resource = resourceService.getResource(resourceId)
            if (!resource.visible)
                throw EntityNotFoundException()

            model.addAttribute("resourceName", resource.name)
            userService.addDefaultFields(model, httpServletRequest)
            "regular/resourceReceived"
        } catch (e: Exception) {
            userService.addDefaultFields(model, httpServletRequest)
            model.addAttribute("errorMessage", "Átvétel Nem Sikerült")
            model.addAttribute("errorDescription", "Nincs elfogadott és nem kiadott foglalás ehhez a erőforráshoz!")
            "errorPage"
        }
    }

}
