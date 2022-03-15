package hu.gerviba.borrower.model

import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency
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
    var lastChecked: Long = 0

) {
    @ManyToMany
    @JoinTable
    @IndexedEmbedded
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    open var maintainerDivisions: MutableSet<DivisionEntity> = mutableSetOf()

    @OneToMany
    @JoinTable
    open var bookings: MutableList<BookingEntity> = mutableListOf()
}
