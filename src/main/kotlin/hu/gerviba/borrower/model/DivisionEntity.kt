package hu.gerviba.borrower.model

import org.hibernate.Hibernate
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import javax.persistence.*

@Entity
@Table(name = "divisions")
data class DivisionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @FullTextField
    @Column(nullable = false)
    var name: String = "",

    @Lob
    @Column(nullable = false)
    var introduction: String = "",

    @ManyToOne
    var parentGroup: GroupEntity? = null,

    @Column(nullable = false)
    var visible: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as DivisionEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
