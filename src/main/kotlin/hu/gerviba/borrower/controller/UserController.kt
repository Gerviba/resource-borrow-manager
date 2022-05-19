package hu.gerviba.borrower.controller

import hu.gerviba.borrower.model.UserEntity
import hu.gerviba.borrower.service.ResourceService
import hu.gerviba.borrower.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class UserController(
    private val userService: UserService,
    private val resourceService: ResourceService
) {

    @GetMapping("/profile")
    fun showOwnProfile(model: Model, authentication: Authentication): String {
        val user = userService.addDefaultFields(model, authentication)
        model.addAttribute("user", user)
        return "regular/showOwnProfile"
    }

    @GetMapping("/profile/edit")
    fun editOwnProfile(model: Model, authentication: Authentication): String {
        val user = userService.addDefaultFields(model, authentication)
        model.addAttribute("user", user)
        return "regular/editOwnProfile"
    }

    @PostMapping("/profile/edit")
    fun updateUser(@ModelAttribute dto: UserEntity): String {
        userService.updateUser(dto.id, dto)
        return "redirect:/profile"
    }

    @GetMapping("/profile/requests")
    fun ownRequests(model: Model, authentication: Authentication): String {
        val user = userService.addDefaultFields(model, authentication)
        model.addAttribute("user", user)
        model.addAttribute("requests", resourceService.getBookingsBorrowedByUser(user))
        return "regular/listOwnRequests"
    }

    @GetMapping("/user/{userId}")
    fun showUserProfile(@PathVariable userId: Int, model: Model, authentication: Authentication): String {
        userService.addDefaultFields(model, authentication)
        model.addAttribute("actualUser", userService.getUser(userId))
        return "regular/showUserProfile"
    }

    @GetMapping("/control/logout")
    fun logout(): String {
        return "regular/logout"
    }

}
