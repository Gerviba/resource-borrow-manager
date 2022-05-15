package hu.gerviba.borrower.dto

import hu.gerviba.borrower.model.BookingEntity
import java.io.Serializable
import java.text.SimpleDateFormat

data class BookingEntityDto(
    val id: Int = 0,
    val dateStart: Long = 0,
    val dateEnd: Long = 0,
    val accepeted: Boolean = false,
    val issued: Boolean = false,
    val closed: Boolean = false,
    val borrowerName: String = ""
) : Serializable {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
    }

    constructor(be: BookingEntity) : this(
        be.id,
        be.dateStart,
        be.dateEnd,
        be.accepted,
        be.issued,
        be.closed,
        be.borrower?.name ?: "n/a"
    )

    fun getDateStartString(): String = DATE_FORMAT.format(dateStart)

    fun getDateEndString(): String = DATE_FORMAT.format(dateEnd)

}
