package hu.gerviba.borrower.controller

import hu.gerviba.borrower.model.UserEntity
import hu.gerviba.borrower.service.ResourceService
import hu.gerviba.borrower.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import javax.servlet.http.HttpServletRequest

@Controller
class UserController(
    private val userService: UserService,
    private val resourceService: ResourceService
) {

    @GetMapping("/profile")
    fun showOwnProfile(model: Model, httpServletRequest: HttpServletRequest): String {
        val userId = userService.getAllUsers()[0].id // FIXME: remove mock
        userService.addDefaultFields(model, httpServletRequest)
        model.addAttribute("user", userService.getUser(userId))
        return "regular/showOwnProfile"
    }

    @GetMapping("/profile/edit")
    fun editOwnProfile(model: Model, httpServletRequest: HttpServletRequest): String {
        val userId = userService.getAllUsers()[0].id // FIXME: remove mock
        userService.addDefaultFields(model, httpServletRequest)
        model.addAttribute("user", userService.getUser(userId))
        return "regular/editOwnProfile"
    }

    @PostMapping("/profile/edit")
    fun updateUser(@ModelAttribute dto: UserEntity): String {
        userService.updateUser(dto.id, dto)
        return "redirect:/profile"
    }

    @GetMapping("/profile/requests")
    fun ownRequests(model: Model, httpServletRequest: HttpServletRequest): String {
        val user = userService.getAllUsers()[0] // FIXME: remove mock
        userService.addDefaultFields(model, httpServletRequest)
        model.addAttribute("user", user)
        model.addAttribute("requests", resourceService.getBookingsBorrowedByUser(user))
        return "regular/listOwnRequests"
    }

    @GetMapping("/user/{userId}")
    fun showUserProfile(@PathVariable userId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        userService.addDefaultFields(model, httpServletRequest)
        model.addAttribute("actualUser", userService.getUser(userId))
        return "regular/showUserProfile"
    }

    @GetMapping("/receive")
    fun receiveBooking(model: Model, httpServletRequest: HttpServletRequest): String {
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/receiveBooking"
    }

    @GetMapping("/receive/{id}")
    fun receiveBooking(
        @PathVariable(required = false) id: Int?,
        model: Model,
        httpServletRequest: HttpServletRequest
    ): String {
        userService.addDefaultFields(model, httpServletRequest)
        return "regular/receiveBooking"
    }

}
