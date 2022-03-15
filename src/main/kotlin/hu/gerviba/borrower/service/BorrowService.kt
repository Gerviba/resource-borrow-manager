package hu.gerviba.borrower.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class BorrowService(

) {

    @Transactional(readOnly = true)
    open fun getBorrowsForItem() {
    }

    @Transactional(readOnly = true)
    open fun getBorrowsForDivision() {
    }

    @Transactional(readOnly = true)
    open fun getBorrowsForGroup() {
    }

    @Transactional(readOnly = true)
    open fun getBorrowsForUser() {
    }



}
