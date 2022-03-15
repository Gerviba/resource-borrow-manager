package hu.gerviba.borrower.model

import javax.persistence.*

@Entity
@Table(name = "resources", indexes = [
    Index(name = "idx_resourceentity_code", columnList = "code")
],  uniqueConstraints = [
    UniqueConstraint(name = "uc_resourceentity_code", columnNames = ["code"])
])
data class ResourceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false, unique = true)
    var code: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Lob
    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var visible: Boolean = false,

    @Column(nullable = false)
    var available: Boolean = false,

    @Column(nullable = false)
    var storageRoom: String = "",

    @ManyToOne
    @JoinColumn
    var ownerGroup: GroupEntity? = null,

    @ManyToMany
    @JoinTable
    var maintainerDivisions: MutableSet<DivisionEntity> = mutableSetOf(),

    @OneToMany
    @JoinTable
    var bookings: MutableList<BookingEntity> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    var created: Long = 0,

    @Column(nullable = false)
    var lastUpdated: Long = 0,

    @Column(nullable = false)
    var lastChecked: Long = 0

)
