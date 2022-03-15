package hu.gerviba.borrower.service

import org.springframework.stereotype.Service

@Service
class TimeService {

    fun now() = System.currentTimeMillis()

}
