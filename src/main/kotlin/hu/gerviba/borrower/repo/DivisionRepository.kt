@file:Suppress("FunctionName")

package hu.gerviba.borrower.repo

import hu.gerviba.borrower.model.DivisionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@SuppressWarnings("kotlin:S100") // Underscore in JPA query method names
interface DivisionRepository : JpaRepository<DivisionEntity, Int> {

    fun findAllByParentGroup_Id(groupId: Int): List<DivisionEntity>

}
