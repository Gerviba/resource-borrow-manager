package hu.gerviba.borrower.repo

import hu.gerviba.borrower.model.GroupEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<GroupEntity, Int> {
}
