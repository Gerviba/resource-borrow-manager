package hu.gerviba.borrower.service

import hu.gerviba.borrower.model.DivisionEntity
import hu.gerviba.borrower.model.ResourceEntity
import hu.gerviba.borrower.repo.DivisionRepository
import hu.gerviba.borrower.repo.GroupRepository
import hu.gerviba.borrower.repo.ResourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
open class ResourceService(
    val resourceRepository: ResourceRepository,
    val divisionRepository: DivisionRepository,
    val groupRepository: GroupRepository,
    val clock: TimeService
) {

    @Transactional(readOnly = false, isolation = Isolation.READ_UNCOMMITTED)
    open fun createResource(dto: ResourceEntity, groupId: Int) {
        resourceRepository.save(createEntityWithDefensiveCopy(dto, groupId))
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun createResourceAndAttach(dto: ResourceEntity, groupId: Int, divisionId: Int) {
        val entity = createEntityWithDefensiveCopy(dto, groupId)
        entity.maintainerDivisions.add(divisionRepository.getById(divisionId))
        resourceRepository.save(entity)
    }

    private fun createEntityWithDefensiveCopy(
        dto: ResourceEntity,
        groupId: Int
    ) = ResourceEntity(
        name = dto.name,
        code = dto.code,
        description = dto.description,
        visible = dto.visible,
        available = dto.available,
        storageRoom = dto.storageRoom,
        ownerGroup = groupRepository.getById(groupId),
        lastUpdated = clock.now(),
        created = clock.now(),
        lastChecked = 0
    )

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllOfGroup(groupId: Int): List<ResourceEntity> {
        return resourceRepository.findAllByOwnerGroup_Id(groupId)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getResource(resourceId: Int): ResourceEntity {
        return resourceRepository.getById(resourceId).copy(maintainerDivisions = mutableSetOf())
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun updateResource(divisionId: Int, resource: ResourceEntity) {
        resourceRepository.save(resourceRepository.getById(divisionId).apply {
            name = resource.name
            code = resource.code // FIXME: might throw exception, unique
            description = resource.description
            visible = resource.visible
            available = resource.available
            storageRoom = resource.storageRoom
            lastUpdated = clock.now()
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun grantAccessToDivision(resourceId: Int, divisionId: Int) {
        resourceRepository.save(resourceRepository.getById(resourceId).apply {
            maintainerDivisions.add(divisionRepository.getById(divisionId))
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun revokeAccessFromDivision(resourceId: Int, divisionId: Int) {
        resourceRepository.save(resourceRepository.getById(resourceId).apply {
            maintainerDivisions.remove(divisionRepository.getById(divisionId))
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getMaintainersOfResource(resourceId: Int): MutableSet<DivisionEntity> {
        return resourceRepository.getById(resourceId).maintainerDivisions
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllForDivision(divisionId: Int): List<ResourceEntity> {
        return resourceRepository.findAllByMaintainerDivisionsId(divisionId)
    }

}
