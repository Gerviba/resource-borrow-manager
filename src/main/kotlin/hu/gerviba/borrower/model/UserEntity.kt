package hu.gerviba.borrower.model

import hu.gerviba.borrower.converter.StringListConverter
import javax.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var neptun: String = "",

    @Column(nullable = false)
    var teams: String = "",

    @Column(nullable = false)
    var email: String = "",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.GUEST,

    @Column(nullable = false)
    var room: String = "",

    @Column(nullable = false)
    var mobile: String = "",

    @Column(nullable = false)
    var locked: Boolean = false,

    @Column(nullable = false)
    @Convert(converter = StringListConverter::class)
    var permissions: MutableList<String> = mutableListOf(),

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
    @JoinTable
    var groups: MutableSet<GroupEntity> = mutableSetOf(),

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
    @JoinTable
    var divisions: MutableSet<DivisionEntity> = mutableSetOf(),

)
