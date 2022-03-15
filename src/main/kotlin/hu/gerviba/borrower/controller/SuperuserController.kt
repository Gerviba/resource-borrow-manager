package hu.gerviba.borrower.controller

import hu.gerviba.borrower.model.GroupEntity
import hu.gerviba.borrower.model.UserEntity
import hu.gerviba.borrower.service.GroupService
import hu.gerviba.borrower.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/su")
class SuperuserController(
    private val groupService: GroupService,
    private val userService: UserService,
) {

    @GetMapping("")
    fun index(): String {
        return "/su/suIndex"
    }

    @GetMapping("/groups")
    fun showGroups(model: Model): String {
        model.addAttribute("groups", groupService.listAll())
        return "su/showGroups"
    }

    @GetMapping("/group/{groupId}")
    fun groupManagement(@PathVariable groupId: Int, model: Model): String {
        model.addAttribute("group", groupService.getGroup(groupId))
        return "su/showSingleGroup"
    }

    @GetMapping("/group/{groupId}/edit")
    fun updateGroup(@PathVariable groupId: Int, model: Model): String {
        model.addAttribute("group", groupService.getGroup(groupId))
        return "su/editGroup"
    }

    @PostMapping("/group/edit")
    fun updateGroupFormTarget(@ModelAttribute group: GroupEntity): String {
        groupService.updateGroup(group.id, group)
        return "redirect:/su/groups"
    }

    @GetMapping("/group")
    fun createGroup(model: Model): String {
        model.addAttribute("newGroup", GroupEntity())
        return "su/createGroup"
    }

    @PostMapping("/group")
    fun createGroupFormTarget(@ModelAttribute group: GroupEntity): String {
        groupService.createGroup(group)
        return "redirect:/su/groups"
    }

    @GetMapping("/users")
    fun showUsers(model: Model): String {
        model.addAttribute("users", userService.getAllUsers())
        return "su/showUsers"
    }

    @GetMapping("/user/{userId}")
    fun userManagement(@PathVariable userId: Int, model: Model): String {
        model.addAttribute(
            "user", userService.getUser(userId)
                .copy(groups = mutableSetOf(), divisions = mutableSetOf())
        )
        return "su/editUser"
    }

    @PostMapping("/user/edit")
    fun updateUser(@ModelAttribute dto: UserEntity): String {
        userService.updateUserByAdmin(dto.id, dto)
        return "redirect:/su/users"
    }

}
