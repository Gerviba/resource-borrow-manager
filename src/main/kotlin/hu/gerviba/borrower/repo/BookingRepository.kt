package hu.gerviba.borrower.repo

import hu.gerviba.borrower.model.BookingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : JpaRepository<BookingEntity, Int> {



}
