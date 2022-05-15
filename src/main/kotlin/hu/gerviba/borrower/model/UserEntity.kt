package hu.gerviba.borrower.model

import hu.gerviba.borrower.converter.StringListConverter
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var internalId: String = "",

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

    @ManyToMany(cascade = [CascadeType.MERGE])
    @JoinTable
    var groups: MutableSet<GroupEntity> = mutableSetOf(),

    @ManyToMany(cascade = [CascadeType.MERGE])
    @JoinTable
    var divisions: MutableSet<DivisionEntity> = mutableSetOf(),

) : Cloneable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

    fun isAdmin() = role == UserRole.ADMIN || role == UserRole.SUPERUSER

    fun hasDivisions() = divisions.isNotEmpty()

}
