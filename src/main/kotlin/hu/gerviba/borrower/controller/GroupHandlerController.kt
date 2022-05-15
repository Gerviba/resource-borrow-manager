package hu.gerviba.borrower.controller

import hu.gerviba.borrower.exception.BusinessOperationFailedException
import hu.gerviba.borrower.service.ResourceService
import hu.gerviba.borrower.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest

@Controller
class GroupHandlerController(
    private val resourceService: ResourceService,
    private val userService: UserService,
) {

    @GetMapping("/requests")
    fun requests(model: Model, httpServletRequest: HttpServletRequest): String {
        userService.addDefaultFields(model, httpServletRequest)
        val user = userService.getAllUsers()[0] // FIXME: remove mock
        model.addAttribute("requests", resourceService.getBookingsHandledByUser(user))
        return "regular/listRequests"
    }

    @GetMapping("/request/{requestId}")
    fun showRequest(model: Model, @PathVariable requestId: Int, httpServletRequest: HttpServletRequest): String {
        val user = userService.getAllUsers()[0] // FIXME: remove mock

        val booking = resourceService.getBooking(requestId)
        if (resourceService.getBookingsHandledByUser(user).none { it.id == requestId } && booking.borrower?.id != user.id)
            throw BusinessOperationFailedException("Insufficient permissions")

        userService.addDefaultFields(model, httpServletRequest)
        model.addAttribute("booking", booking)

        return "regular/showBooking"
    }

    @PostMapping("/request/{requestId}/accept")
    fun acceptRequest(@PathVariable requestId: Int, httpServletRequest: HttpServletRequest): String {
        val user = userService.getAllUsers()[0] // FIXME: remove mock
        if (resourceService.getBookingsHandledByUser(user).none { it.id == requestId })
            throw BusinessOperationFailedException("Insufficient permissions")

        resourceService.updateBookingIfExists(requestId) {
            it.accepted = true
            it.rejected = false
            it.handlerAdministerOutbound = user
        }
        return "redirect:/requests"
    }

    @PostMapping("/request/{requestId}/reject")
    fun rejectRequest(@PathVariable requestId: Int, httpServletRequest: HttpServletRequest): String {
        val user = userService.getAllUsers()[0] // FIXME: remove mock
        if (resourceService.getBookingsHandledByUser(user).none { it.id == requestId })
            throw BusinessOperationFailedException("Insufficient permissions")

        resourceService.updateBookingIfExists(requestId) {
            it.accepted = false
            it.rejected = true
            it.handlerAdministerOutbound = null
        }
        return "redirect:/requests"
    }

    @GetMapping("/return")
    fun returnBooking(model: Model, httpServletRequest: HttpServletRequest): String {
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/returnBooking"
    }

    @GetMapping("/return/confirm/{codeBase64}")
    fun receiveResource(@PathVariable codeBase64: String, httpServletRequest: HttpServletRequest): String {
        val code = resolveCode(codeBase64)

        val user = userService.getAllUsers()[0] // FIXME: mock
        return resourceService.returnResource(user, code)
            .map { pair -> "redirect:/resource-returned/${pair.first.id}/${pair.second.id}" }
            .orElse("redirect:/resource-returned/0/0")
    }

    @GetMapping("/resource-returned/{resourceId}/{bookingId}")
    fun bookingClosed(model: Model, @PathVariable resourceId: Int, @PathVariable bookingId: Int, httpServletRequest: HttpServletRequest): String {
        return try {
            val resource = resourceService.getResource(resourceId)
            if (!resource.visible)
                throw EntityNotFoundException()

            model.addAttribute("resourceName", resource.name)
            model.addAttribute("bookingId", bookingId)
            userService.addDefaultFields(model, httpServletRequest)
            "regular/returnAddComment"
        } catch (e: Exception) {
            userService.addDefaultFields(model, httpServletRequest)
            model.addAttribute("errorMessage", "Visszavétel Nem Sikerült")
            model.addAttribute("errorDescription", "Nincs átvett és nem visszavett foglalás ehhez az erőforráshoz!")
            "errorPage"
        }
    }

    @PostMapping("/return/add-comment/{bookingId}")
    fun addCommentToBooking(@PathVariable bookingId: Int, @RequestParam comment: String): String {
        val user = userService.getAllUsers()[0] // FIXME: mock
        resourceService.addCommentIfApplicable(user, bookingId, comment)
        return "redirect:/requests"
    }

    @GetMapping("/inventory")
    fun inventiry(model: Model, httpServletRequest: HttpServletRequest): String {
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/inventoryCam"
    }

    @ResponseBody
    @GetMapping("/inventory/check/{codeBase64}")
    fun inventoryCheck(@PathVariable codeBase64: String): String {
        return resourceService.updateInventoryCheck(resolveCode(codeBase64))
    }

}

fun resolveCode(codeBase64: String): String {
    var code = try {
        String(Base64.getDecoder().decode(codeBase64))
    } catch (e: Exception) {
        "" // empty string
    }

    if (code.startsWith("http://") || code.startsWith("https://")) {
        val split = code.split('/')
        code = split[split.size - 1]
    }
    return code
}
