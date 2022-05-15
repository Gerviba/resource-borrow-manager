package hu.gerviba.borrower.model

import org.hibernate.Hibernate
import java.text.SimpleDateFormat
import javax.persistence.*

@Entity
@Table(name = "booking", indexes = [
    Index(name = "idx_bookingentity_realDate", columnList = "realDateStart, realDateEnd"),
    Index(name = "idx_bookingentity_date", columnList = "dateStart, dateEnd"),
    Index(name = "idx_bookingentity_resource_id", columnList = "resource_id"),
    Index(name = "idx_bookingentity_borrower_id", columnList = "borrower_id")
])
class BookingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @ManyToOne
    @JoinColumn(updatable = false)
    var resource: ResourceEntity? = null,

    @ManyToOne
    @JoinColumn(updatable = false)
    var borrower: UserEntity? = null,

    @ManyToOne
    @JoinColumn
    var handlerAdministerOutbound: UserEntity? = null,

    @ManyToOne
    @JoinColumn
    var handlerAdministerInbound: UserEntity? = null,

    @Column(nullable = false)
    var dateStart: Long = 0,

    @Column(nullable = false)
    var dateEnd: Long = 0,

    @Column(nullable = false)
    var realDateStart: Long = 0,

    @Column(nullable = false)
    var realDateEnd: Long = 0,

    @Column(nullable = false)
    var accepted: Boolean = false,

    @Column(nullable = false)
    var rejected: Boolean = false,

    @Column(nullable = false)
    var issued: Boolean = false,

    @Column(nullable = false)
    var closed: Boolean = false,

    @Lob
    @Column(nullable = false)
    var borrowerComment: String = "",

    @Lob
    @Column(nullable = false)
    var receptionComment: String = "",

    @Lob
    @Column(nullable = false)
    var recaptureComment: String = "",
) {
    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
    }

    fun getDateStartString(): String = DATE_FORMAT.format(dateStart)

    fun getDateEndString(): String = DATE_FORMAT.format(dateEnd)

    fun getRealDateStartString(): String = DATE_FORMAT.format(realDateStart)

    fun getRealDateEndString(): String = DATE_FORMAT.format(realDateEnd)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BookingEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}
