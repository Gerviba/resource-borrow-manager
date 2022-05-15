@file:Suppress("FunctionName")

package hu.gerviba.borrower.repo

import hu.gerviba.borrower.model.DivisionEntity
import hu.gerviba.borrower.model.ResourceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@SuppressWarnings("kotlin:S100") // Underscore in JPA query method names
interface ResourceRepository : JpaRepository<ResourceEntity, Int> {

    fun findAllByOwnerGroup_Id(groupId: Int): List<ResourceEntity>

    fun findAllByMaintainerDivisionsId(division: Int): List<ResourceEntity>

    fun getByIdAndVisibleTrue(id: Int): ResourceEntity

    fun existsByCode(code: String): Boolean

    @Query("select r from ResourceEntity r where :division member of r.maintainerDivisions")
    fun findAllByMaintainerDivisionsContains(division: DivisionEntity): List<ResourceEntity>

    fun findByCodeAndVisibleTrue(code: String): Optional<ResourceEntity>

    fun findByCodeAndVisibleTrueAndAvailableTrue(code: String): Optional<ResourceEntity>

    fun findByCode(code: String): Optional<ResourceEntity>

}
