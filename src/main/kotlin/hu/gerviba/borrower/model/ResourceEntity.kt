package hu.gerviba.borrower.model

import org.hibernate.Hibernate
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency
import java.text.SimpleDateFormat
import javax.persistence.*

@Entity
@Table(name = "resources", indexes = [
    Index(name = "idx_resourceentity_code", columnList = "code")
],  uniqueConstraints = [
    UniqueConstraint(name = "uc_resourceentity_code", columnNames = ["code"])
])
@Indexed
data class ResourceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false, unique = true)
    var code: String = "",

    @FullTextField
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
    @IndexedEmbedded
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    var ownerGroup: GroupEntity? = null,

    @Column(nullable = false, updatable = false)
    var created: Long = 0,

    @Column(nullable = false)
    var lastUpdated: Long = 0,

    @Column(nullable = false)
    var lastChecked: Long = 0,

    @ManyToMany
    @JoinTable
    @IndexedEmbedded
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    var maintainerDivisions: MutableSet<DivisionEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "resource")
    var bookings: MutableList<BookingEntity> = mutableListOf(),

    @Column(nullable = false)
    var imageName: String = "",
) {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
    }

    fun getCreatedString(): String = DATE_FORMAT.format(created)

    fun getLastUpdatedString(): String = DATE_FORMAT.format(lastUpdated)

    fun getLastCheckedString(): String = DATE_FORMAT.format(lastChecked)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ResourceEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
