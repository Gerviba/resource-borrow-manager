@file:Suppress("FunctionName")

package hu.gerviba.borrower.repo

import hu.gerviba.borrower.model.ResourceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@SuppressWarnings("kotlin:S100") // Underscore in JPA query method names
interface ResourceRepository : JpaRepository<ResourceEntity, Int> {

    fun findAllByOwnerGroup_Id(groupId: Int): List<ResourceEntity>

    fun findAllByMaintainerDivisionsId(division: Int): List<ResourceEntity>

}
