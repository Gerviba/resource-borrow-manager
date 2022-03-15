package hu.gerviba.borrower.service

import hu.gerviba.borrower.model.DivisionEntity
import hu.gerviba.borrower.repo.DivisionRepository
import hu.gerviba.borrower.repo.GroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
open class DivisionService(
    val divisionRepository: DivisionRepository,
    val groupRepository: GroupRepository
) {

    @Transactional(readOnly = false, isolation = Isolation.READ_UNCOMMITTED)
    open fun createDivision(dto: DivisionEntity, groupId: Int) {
        divisionRepository.save(
            DivisionEntity(
                name = dto.name,
                introduction = dto.introduction,
                parentGroup = groupRepository.getById(groupId),
                visible = dto.visible
            )
        )
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun listAllOfGroup(groupId: Int): List<DivisionEntity> {
        return divisionRepository.findAllByParentGroup_Id(groupId)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getDivision(divisionId: Int): DivisionEntity {
        return divisionRepository.getById(divisionId).copy()
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun updateDivision(divisionId: Int, division: DivisionEntity) {
        divisionRepository.save(divisionRepository.getById(divisionId).apply {
            name = division.name
            introduction = division.introduction
            visible = division.visible
        })
    }

}
