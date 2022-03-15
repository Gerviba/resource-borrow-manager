package hu.gerviba.borrower.controller

import hu.gerviba.borrower.service.DivisionService
import hu.gerviba.borrower.service.ResourceService
import hu.gerviba.borrower.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/division")
class DivisionController(
    private val divisionService: DivisionService,
    private val resourceService: ResourceService,
    private val userService: UserService
) {

    @GetMapping("/{divisionId}")
    fun showDivision(@PathVariable divisionId: Int, model: Model): String {
        model.addAttribute("division", divisionService.getDivision(divisionId))
        model.addAttribute("resources", resourceService.getAllForDivision(divisionId))
        model.addAttribute("members", userService.getAllFromDivision(divisionId))
        return "showDivision"
    }

}
