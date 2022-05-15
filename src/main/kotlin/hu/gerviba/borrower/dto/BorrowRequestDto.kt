package hu.gerviba.borrower.dto

class BorrowRequestDto(
    var dateStart: Long = 0,
    var dateEnd: Long = 0,
    var comment: String = ""
)
