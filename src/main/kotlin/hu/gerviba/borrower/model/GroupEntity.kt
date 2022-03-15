package hu.gerviba.borrower.model

import javax.persistence.*

@Entity
@Table(name = "groups")
data class GroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var website: String = "",

    @Column(nullable = false)
    var introduction: String = "",

    @Column(nullable = false)
    var visible: Boolean = false
)
