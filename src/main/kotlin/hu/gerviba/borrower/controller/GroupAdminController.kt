package hu.gerviba.borrower.controller

import hu.gerviba.borrower.config.AppConfig
import hu.gerviba.borrower.model.DivisionEntity
import hu.gerviba.borrower.model.GroupEntity
import hu.gerviba.borrower.model.ResourceEntity
import hu.gerviba.borrower.service.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.ThreadLocalRandom
import javax.servlet.http.HttpServletRequest

const val ID_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

@Controller
@RequestMapping("/admin")
class GroupAdminController(
    private val groupService: GroupService,
    private val divisionService: DivisionService,
    private val resourceService: ResourceService,
    private val userService: UserService,
    private val config: AppConfig
) {

    @GetMapping("")
    fun adminIndex(model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("groups", groupService.getAll()) // FIXME: getAll that is owned by current user
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/adminIndex"
    }

    @GetMapping("/group/{groupId}/divisions")
    fun listDivisions(@PathVariable groupId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("divisions", divisionService.listAllOfGroup(groupId))
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/showDivisions"
    }

    /// DIVISIONS

    @GetMapping("/group/{groupId}")
    fun index(@PathVariable groupId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("group", groupService.getGroup(groupId))
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/showGroup"
    }

    @GetMapping("/group/{groupId}/division/{divisionId}/edit")
    fun updateDivision(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        model: Model,
        httpServletRequest: HttpServletRequest
    ): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("division", divisionService.getDivision(divisionId))
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/editDivision"
    }

    @PostMapping("/group/{groupId}/division/{divisionId}/edit")
    fun updateDivisionFormTarget(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        @ModelAttribute division: DivisionEntity,
        httpServletRequest: HttpServletRequest
    ): String {
        divisionService.updateDivision(division.id, division)
        return "redirect:/admin/group/${groupId}/divisions"
    }

    @GetMapping("/group/{groupId}/division")
    fun createDivision(@PathVariable groupId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("newDivision", GroupEntity())
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/createDivision"
    }

    @PostMapping("/group/{groupId}/division")
    fun createDivisionFormTarget(
        @PathVariable groupId: Int,
        @ModelAttribute division: DivisionEntity,
        httpServletRequest: HttpServletRequest
    ): String {
        divisionService.createDivision(division, groupId)
        return "redirect:/admin/group/${groupId}/divisions"
    }

    @GetMapping("/group/{groupId}/division/{divisionId}/resources")
    fun listResourcesOfDivision(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        model: Model,
        httpServletRequest: HttpServletRequest
    ): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resources", resourceService.getAllForDivision(divisionId))
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/showResources"
    }

    /// RESOURCES

    @GetMapping("/group/{groupId}/resources")
    fun listResources(@PathVariable groupId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resources", resourceService.getAllOfGroup(groupId))
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/showResources"
    }

    @GetMapping("/group/{groupId}/resource/{resourceId}/edit")
    fun editResource(
        @PathVariable groupId: Int,
        @PathVariable resourceId: Int,
        model: Model,
        httpServletRequest: HttpServletRequest
    ): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resource", resourceService.getResource(resourceId))
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/editResource"
    }

    @PostMapping("/group/{groupId}/resource/{resourceId}/edit")
    fun editResourceFormTarget(
        @ModelAttribute resource: ResourceEntity,
        @PathVariable resourceId: Int,
        @PathVariable groupId: String,
        httpServletRequest: HttpServletRequest,
        @RequestParam(required = false) file: MultipartFile?
    ): String {
        file?.uploadFile("resource")?.let { fileName -> resource.imageName = "resource/$fileName" }
        resourceService.updateResource(resource.id, resource)
        return "redirect:/admin/group/${groupId}/resources"
    }

    @GetMapping("/group/{groupId}/resource")
    fun createResource(@PathVariable groupId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("newResource", ResourceEntity())
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/createResource"
    }

    @PostMapping("/group/{groupId}/resource")
    fun createResourceFormTarget(
        @PathVariable groupId: Int,
        @ModelAttribute resource: ResourceEntity,
        httpServletRequest: HttpServletRequest,
        @RequestParam(required = false) file: MultipartFile?
    ): String {
        file?.uploadFile("resource")?.let { fileName -> resource.imageName = "resource/$fileName" }
        resourceService.createResource(resource, groupId)
        return "redirect:/admin/group/${groupId}/resources"
    }

    @GetMapping("/group/{groupId}/division/{divisionId}/resource")
    fun createResourceForDivision(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        model: Model,
        httpServletRequest: HttpServletRequest
    ): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("divisionId", divisionId)
        model.addAttribute("divisionName", divisionService.getDivision(divisionId).name)
        model.addAttribute("newResource", ResourceEntity())
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/createResourceForDivision"
    }

    @PostMapping("/group/{groupId}/division/{divisionId}/resource")
    fun createResourceForDivisionFormTarget(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        @ModelAttribute resource: ResourceEntity,
        httpServletRequest: HttpServletRequest
    ): String {
        resourceService.createResourceAndAttach(resource, groupId, divisionId)
        return "redirect:/admin/group/${groupId}/division/${divisionId}/resources"
    }

    @GetMapping("/group/{groupId}/resource/{resourceId}/access")
    fun accessResource(
        @PathVariable groupId: Int,
        @PathVariable resourceId: Int,
        model: Model,
        httpServletRequest: HttpServletRequest
    ): String {
        model.addAttribute("groupId", groupId)
        model.addAttribute("resourceId", resourceId)
        val maintainerDivisions = resourceService.getMaintainersOfResource(resourceId)
        model.addAttribute("ownerDivisions", maintainerDivisions)
        model.addAttribute("availableDivisions", divisionService.listAllOfGroup(groupId).filter { !maintainerDivisions.contains(it) })
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/accessResource"
    }

    @PostMapping("/group/{groupId}/resource/{resourceId}/access/grant")
    fun grantResourceAccess(
        @PathVariable groupId: Int,
        @PathVariable resourceId: Int,
        @RequestParam id: Int,
        httpServletRequest: HttpServletRequest
    ): String {
        resourceService.grantAccessToDivision(resourceId, id)
        return "redirect:/admin/group/${groupId}/resource/${resourceId}/access"
    }

    @PostMapping("/group/{groupId}/resource/{resourceId}/access/revoke")
    fun revokeResourceAccess(
        @PathVariable groupId: Int,
        @PathVariable resourceId: Int,
        @RequestParam id: Int,
        httpServletRequest: HttpServletRequest
    ): String {
        resourceService.revokeAccessFromDivision(resourceId, id)
        return "redirect:/admin/group/${groupId}/resource/${resourceId}/access"
    }

    @ResponseBody
    @GetMapping("/generate/resource-id")
    fun generateUniqueId(): String {
        var id = generateAnId()
        while (resourceService.existsByCode(id))
            id = generateAnId()
        return id
    }

    private fun generateAnId(): String {
        val threadLocalRandom = ThreadLocalRandom.current()
        var id = ""
        for (i in 0 until config.generatedIdLength)
            id += ID_DIGITS[threadLocalRandom.nextInt(ID_DIGITS.length)]
        return id
    }

    /// USERS

    @GetMapping("/group/{groupId}/users")
    fun users(@PathVariable groupId: Int, model: Model, httpServletRequest: HttpServletRequest): String {
        val users = userService.getAllFromGroup(groupId)
        model.addAttribute("users", users)
        model.addAttribute("availableUsers", userService.getAllUsers().filter { !users.contains(it) })
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/showUsersOfGroup"
    }

    @PostMapping("/group/{groupId}/user/add")
    fun addUser(@PathVariable groupId: Int, @RequestParam userId: Int, httpServletRequest: HttpServletRequest): String {
        userService.addUserToGroup(userId, groupId)
        return "redirect:/admin/group/${groupId}/users"
    }

    @PostMapping("/group/{groupId}/user/kick")
    fun kickUser(@PathVariable groupId: Int, @RequestParam userId: Int, httpServletRequest: HttpServletRequest): String {
        userService.kickUserOutOfGroup(userId, groupId)
        return "redirect:/admin/group/${groupId}/users"
    }

    @GetMapping("/group/{groupId}/division/{divisionId}/users")
    fun usersOfDivision(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        model: Model,
        httpServletRequest: HttpServletRequest
    ): String {
        val users = userService.getAllFromDivision(divisionId)
        model.addAttribute("users", users)
        model.addAttribute("divisionName", divisionService.getDivision(divisionId).name)
        model.addAttribute("availableUsers", userService.getAllFromGroup(groupId).filter { !users.contains(it) })
        userService.addDefaultFields(model, httpServletRequest)
        return "admin/showUsersOfDivision"
    }

    @PostMapping("/group/{groupId}/division/{divisionId}/user/add")
    fun addUserToDivision(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        @RequestParam userId: Int,
        httpServletRequest: HttpServletRequest
    ): String {
        userService.addUserToDivision(userId, divisionId)
        return "redirect:/admin/group/${groupId}/division/${divisionId}/users"
    }

    @PostMapping("/group/{groupId}/division/{divisionId}/user/kick")
    fun kickUserOutOfDivision(
        @PathVariable groupId: Int,
        @PathVariable divisionId: Int,
        @RequestParam userId: Int,
        httpServletRequest: HttpServletRequest
    ): String {
        userService.kickUserOutOfDivision(userId, divisionId)
        return "redirect:/admin/group/${groupId}/division/${divisionId}/users"
    }

}
