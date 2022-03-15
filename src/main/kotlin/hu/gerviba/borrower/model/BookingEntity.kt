package hu.gerviba.borrower.model

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

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(updatable = false)
    var borrower: UserEntity? = null,

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn
    var handlerAdministerOutbound: UserEntity? = null,

    @ManyToOne(cascade = [CascadeType.PERSIST])
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
    var accepeted: Boolean = false,

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
)
