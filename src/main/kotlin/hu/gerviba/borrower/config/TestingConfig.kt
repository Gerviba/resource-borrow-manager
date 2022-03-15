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
        val group1 = GroupEntity(name = "group1")
        groupRepository.save(group1)
        val group2 = GroupEntity(name = "group2")
        groupRepository.save(group2)

        val div1 = DivisionEntity(name = "g1_div1", parentGroup = group1)
        divisionRepository.save(div1)
        val div2 = DivisionEntity(name = "g1_div2", parentGroup = group1)
        divisionRepository.save(div2)
        val div3 = DivisionEntity(name = "g2_div3", parentGroup = group2)
        divisionRepository.save(div3)
        val div4 = DivisionEntity(name = "g2_div4", parentGroup = group2)
        divisionRepository.save(div4)

        val res1 = ResourceEntity(name = "g1_res1", ownerGroup = group1)
        resourceRepository.save(res1)

        val user1 = UserEntity(name = "User 1")
        userRepository.save(user1)
        val user2 = UserEntity(name = "User 2")
        userRepository.save(user2)
        val user3 = UserEntity(name = "User 3")
        userRepository.save(user3)
    }

}
