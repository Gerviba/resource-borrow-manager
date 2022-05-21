package hu.gerviba.borrower.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.FORBIDDEN)
class AdminAccessDeniedException : RuntimeException()
