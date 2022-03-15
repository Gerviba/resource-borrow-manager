package hu.gerviba.borrower.config

import hu.gerviba.borrower.model.DivisionEntity
import hu.gerviba.borrower.model.GroupEntity
import hu.gerviba.borrower.model.ResourceEntity
import hu.gerviba.borrower.model.UserEntity
import hu.gerviba.borrower.repo.DivisionRepository
import hu.gerviba.borrower.repo.GroupRepository
import hu.gerviba.borrower.repo.ResourceRepository
import hu.gerviba.borrower.repo.UserRepository
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class TestingConfig(
    val groupRepository: GroupRepository,
    val divisionRepository: DivisionRepository,
    val resourceRepository: ResourceRepository,
    val userRepository: UserRepository
) {

    @PostConstruct
    fun init() {
        val group1 = GroupEntity(name = "AUT")
        groupRepository.save(group1)
        val group2 = GroupEntity(name = "VET")
        groupRepository.save(group2)
        val group3 = GroupEntity(name = "AIT")
        groupRepository.save(group3)

        val div1 = DivisionEntity(name = "g1_div1", parentGroup = group1)
        divisionRepository.save(div1)
        val div2 = DivisionEntity(name = "g1_div2", parentGroup = group1)
        divisionRepository.save(div2)
        val div3 = DivisionEntity(name = "nagyfesz labor", parentGroup = group2)
        divisionRepository.save(div3)
        val div4 = DivisionEntity(name = "kisfesz labor", parentGroup = group2)
        divisionRepository.save(div4)

        val res1 = ResourceEntity(name = "villám", ownerGroup = group2, code = "C1")
        resourceRepository.save(res1)
        val res2 = ResourceEntity(name = "amperok", ownerGroup = group2, code = "C2")
        resourceRepository.save(res2)
        val res3 = ResourceEntity(name = "voltok", ownerGroup = group2, code = "C3")
        resourceRepository.save(res3)

        resourceRepository.save(ResourceEntity(name = "automatizálás", ownerGroup = group1, code = "C4"))
        resourceRepository.save(ResourceEntity(name = "alkalmazás", ownerGroup = group1, code = "C5"))
        resourceRepository.save(ResourceEntity(name = "informatika", ownerGroup = group1, code = "C6"))

        val user1 = UserEntity(name = "User 1")
        userRepository.save(user1)
        val user2 = UserEntity(name = "User 2")
        userRepository.save(user2)
        val user3 = UserEntity(name = "User 3")
        userRepository.save(user3)
    }

}
