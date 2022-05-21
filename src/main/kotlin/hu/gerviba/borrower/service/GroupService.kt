package hu.gerviba.borrower.service

import hu.gerviba.borrower.model.GroupEntity
import hu.gerviba.borrower.repo.GroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
open class GroupService(
    val groupRepository: GroupRepository
) {

    @Transactional(readOnly = false, isolation = Isolation.READ_UNCOMMITTED)
    open fun createGroup(dto: GroupEntity) {
        groupRepository.save(
            GroupEntity(
                name = dto.name,
                introduction = dto.introduction,
                website = dto.website,
                visible = dto.visible
            )
        )
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAll(): List<GroupEntity> {
        return groupRepository.findAll()
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getGroup(groupId: Int): GroupEntity {
        return groupRepository.getById(groupId).copy()
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun updateGroup(groupId: Int, group: GroupEntity) {
        groupRepository.save(groupRepository.getById(groupId).apply {
            name = group.name
            website = group.website
            introduction = group.introduction
            visible = group.visible
        })
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllVisible(): List<GroupEntity> {
        return groupRepository.findAllByVisibleTrue()
    }

}
