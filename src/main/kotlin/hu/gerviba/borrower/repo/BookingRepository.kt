package hu.gerviba.borrower.repo

import hu.gerviba.borrower.model.BookingEntity
import hu.gerviba.borrower.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookingRepository : JpaRepository<BookingEntity, Int> {

    @Query("SELECT b FROM BookingEntity b WHERE b.resource.id = :resourceId " +
            "AND b.accepted = TRUE " +
            "AND b.closed = FALSE " +
            "AND ( " +
                "(b.dateStart BETWEEN :startTime AND :endTime) " +
                "OR (b.dateEnd BETWEEN :startTime AND :endTime) " +
                "OR (b.dateStart < :startTime AND b.dateEnd > :endTime) " +
            ")"
    )
    fun findAllByResourceInInterval(resourceId: Int, startTime: Long, endTime: Long): List<BookingEntity>

    fun findAllByResource_IdAndDateEndGreaterThanAndAcceptedTrue(resourceId: Int, timestamp: Long): List<BookingEntity>

    fun findAllByResource_IdAndAcceptedTrue(resourceId: Int): List<BookingEntity>

    fun findAllByBorrowerOrderByDateStart(user: UserEntity): List<BookingEntity>

    fun findAllByBorrower_IdAndResource_IdAndAcceptedTrueAndRejectedFalseAndIssuedFalseAndClosedFalse(borrowerId: Int, resourceId: Int): List<BookingEntity>

    fun findAllByResource_IdAndIssuedTrueAndClosedFalse(resourceId: Int): List<BookingEntity>
}
