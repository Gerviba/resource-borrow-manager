package hu.gerviba.borrower.controller

import hu.gerviba.borrower.model.DivisionEntity
import hu.gerviba.borrower.model.GroupEntity
import hu.gerviba.borrower.model.ResourceEntity
import hu.gerviba.borrower.service.DivisionService
import hu.gerviba.borrower.service.GroupService
import hu.gerviba.borrower.service.ResourceService
import hu.gerviba.borrower.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/group")
class GroupAdminController(
    private val groupService: GroupService,
    private val divisionService: DivisionService,
    private val resourceService: ResourceService,
    private val userService: UserService,
) {

    @GetMapping("/{groupId}/divisions")
    fun listDivisions(@PathVariable groupId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("divisions", divisionService.listAllOfGroup(groupId))
        return "admin/showDivisions"
    }

    /// DIVISIONS

    @GetMapping("/{groupId}")
    fun index(@PathVariable groupId: Int, model: Model): String {
        model.addAttribute("group", groupService.getGroup(groupId))
        return "admin/showGroup"
    }

    @GetMapping("/{groupId}/division/{divisionId}/edit")
    fun updateDivision(@PathVariable groupId: Int, @PathVariable divisionId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("division", divisionService.getDivision(divisionId))
        return "admin/editDivision"
    }

    @PostMapping("/{groupId}/division/{divisionId}/edit")
    fun updateDivisionFormTarget(@PathVariable groupId: Int, @PathVariable divisionId: Int, @ModelAttribute division: DivisionEntity): String {
        divisionService.updateDivision(division.id, division)
        return "redirect:/admin/group/${groupId}/divisions"
    }

    @GetMapping("/{groupId}/division")
    fun createDivision(@PathVariable groupId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("newDivision", GroupEntity())
        return "admin/createDivision"
    }

    @PostMapping("/{groupId}/division")
    fun createDivisionFormTarget(@PathVariable groupId: Int, @ModelAttribute division: DivisionEntity): String {
        divisionService.createDivision(division, groupId)
        return "redirect:/admin/group/${groupId}/divisions"
    }

    @GetMapping("/{groupId}/division/{divisionId}/resources")
    fun listResourcesOfDivision(@PathVariable groupId: Int, @PathVariable divisionId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resources", resourceService.getAllForDivision(divisionId))
        return "admin/showResources"
    }

    /// RESOURCES

    @GetMapping("/{groupId}/resources")
    fun listResources(@PathVariable groupId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resources", resourceService.getAllOfGroup(groupId))
        return "admin/showResources"
    }

    @GetMapping("/{groupId}/resource/{resourceId}/edit")
    fun editResource(@PathVariable groupId: Int, @PathVariable resourceId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resource", resourceService.getResource(resourceId))
        return "admin/editResource"
    }

    @PostMapping("/{groupId}/resource/{resourceId}/edit")
    fun editResourceFormTarget(@ModelAttribute resource: ResourceEntity, @PathVariable resourceId: Int,
                               @PathVariable groupId: String
    ): String {
        resourceService.updateResource(resource.id, resource)
        return "redirect:/admin/group/${groupId}/resources"
    }

    @GetMapping("/{groupId}/resource")
    fun createResource(@PathVariable groupId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("newResource", ResourceEntity())
        return "admin/createResource"
    }

    @PostMapping("/{groupId}/resource")
    fun createResourceFormTarget(@PathVariable groupId: Int, @ModelAttribute resource: ResourceEntity, model: Model): String {
        resourceService.createResource(resource, groupId)
        return "redirect:/admin/group/${groupId}/resources"
    }

    @GetMapping("/{groupId}/division/{divisionId}/resource")
    fun createResourceForDivision(@PathVariable groupId: Int, @PathVariable divisionId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("divisionId", divisionId)
        model.addAttribute("divisionName", divisionService.getDivision(divisionId).name)
        model.addAttribute("newResource", ResourceEntity())
        return "admin/createResourceForDivision"
    }

    @PostMapping("/{groupId}/division/{divisionId}/resource")
    fun createResourceForDivisionFormTarget(@PathVariable groupId: Int, @PathVariable divisionId: Int, @ModelAttribute resource: ResourceEntity): String {
        resourceService.createResourceAndAttach(resource, groupId, divisionId)
        return "redirect:/admin/group/${groupId}/division/${divisionId}/resources"
    }

    @GetMapping("/{groupId}/resource/{resourceId}/access")
    fun accessResource(@PathVariable groupId: Int, @PathVariable resourceId: Int, model: Model): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resourceId", resourceId)
        val maintainerDivisions = resourceService.getMaintainersOfResource(resourceId)
        model.addAttribute("ownerDivisions", maintainerDivisions)
        model.addAttribute("availableDivisions", divisionService.listAllOfGroup(groupId).filter { !maintainerDivisions.contains(it) })
        return "admin/accessResource"
    }

    @PostMapping("/{groupId}/resource/{resourceId}/access/grant")
    fun grantResourceAccess(@PathVariable groupId: Int, @PathVariable resourceId: Int, @RequestParam id: Int): String {
        resourceService.grantAccessToDivision(resourceId, id)
        return "redirect:/admin/group/${groupId}/resource/${resourceId}/access"
    }

    @PostMapping("/{groupId}/resource/{resourceId}/access/revoke")
    fun revokeResourceAccess(@PathVariable groupId: Int, @PathVariable resourceId: Int, @RequestParam id: Int): String {
        resourceService.revokeAccessFromDivision(resourceId, id)
        return "redirect:/admin/group/${groupId}/resource/${resourceId}/access"
    }

    /// USERS

    @GetMapping("/{groupId}/users")
    fun users(@PathVariable groupId: Int, model: Model): String {
        val users = userService.getAllFromGroup(groupId)
        model.addAttribute("users", users)
        model.addAttribute("availableUsers", userService.getAllUsers().filter { !users.contains(it) })
        return "admin/showUsersOfGroup"
    }

    @PostMapping("/{groupId}/user/add")
    fun addUser(@PathVariable groupId: Int, @RequestParam userId: Int): String {
        userService.addUserToGroup(userId, groupId)
        return "redirect:/admin/group/${groupId}/users"
    }

    @PostMapping("/{groupId}/user/kick")
    fun kickUser(@PathVariable groupId: Int, @RequestParam userId: Int): String {
        userService.kickUserOutOfGroup(userId, groupId)
        return "redirect:/admin/group/${groupId}/users"
    }

    @GetMapping("/{groupId}/division/{divisionId}/users")
    fun usersOfDivision(@PathVariable groupId: Int, @PathVariable divisionId: Int, model: Model): String {
        val users = userService.getAllFromDivision(divisionId)
        model.addAttribute("users", users)
        model.addAttribute("divisionName", divisionService.getDivision(divisionId).name)
        model.addAttribute("availableUsers", userService.getAllFromGroup(groupId).filter { !users.contains(it) })
        return "admin/showUsersOfDivision"
    }

    @PostMapping("/{groupId}/division/{divisionId}/user/add")
    fun addUserToDivision(@PathVariable groupId: Int, @PathVariable divisionId: Int, @RequestParam userId: Int): String {
        userService.addUserToDivision(userId, divisionId)
        return "redirect:/admin/group/${groupId}/users"
    }

    @PostMapping("/{groupId}/division/{divisionId}/user/kick")
    fun kickUserOutOfDivision(@PathVariable groupId: Int, @PathVariable divisionId: Int, @RequestParam userId: Int): String {
        userService.kickUserOutOfDivision(userId, divisionId)
        return "redirect:/admin/group/${groupId}/users"
    }

}
