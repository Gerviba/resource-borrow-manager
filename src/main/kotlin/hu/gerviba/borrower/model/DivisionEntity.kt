package hu.gerviba.borrower.model

import javax.persistence.*

@Entity
@Table(name = "divisions")
data class DivisionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var name: String = "",

    @Lob
    @Column(nullable = false)
    var introduction: String = "",

    @ManyToOne
    var parentGroup: GroupEntity? = null,

    @Column(nullable = false)
    var visible: Boolean = false,
)