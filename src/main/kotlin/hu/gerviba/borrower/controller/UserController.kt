package hu.gerviba.borrower.controller

import hu.gerviba.borrower.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/profile/edit")
    fun editOwnProfile(model: Model): String {
        val userId = userService.getAllUsers()[0].id // FIXME: remove mock
        model.addAttribute("user", userService.getUser(userId))
        return "regular/editOwnProfile"
    }

    @GetMapping("/profile/")
    fun showOwnProfile(model: Model): String {
        val userId = userService.getAllUsers()[0].id // FIXME: remove mock
        model.addAttribute("user", userService.getUser(userId))
        return "regular/showOwnProfile"
    }

    // show own bookings

    // requested resources

    @GetMapping("/user/{userId}")
    fun showUserProfile(@PathVariable userId: Int, model: Model): String {
        model.addAttribute("user", userService.getUser(userId))
        return "regular/showUserProfile"
    }

}
