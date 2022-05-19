package hu.gerviba.borrower.repo

import hu.gerviba.borrower.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, Int> {

    fun findAllByDivisionsId(divisionId: Int): List<UserEntity>

    fun findAllByGroupsId(groupId: Int): List<UserEntity>

    fun findByInternalId(internalId: String): Optional<UserEntity>

}
