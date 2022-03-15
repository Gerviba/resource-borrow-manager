package hu.gerviba.borrower.service

import hu.gerviba.borrower.model.ResourceEntity
import org.hibernate.search.mapper.orm.Search
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager


@Service
open class SearchService(
    private val entityManager: EntityManager
) : ApplicationListener<ApplicationReadyEvent> {

    private val specialChars = Regex("[\"'~+\\-!|]")

    @Transactional(readOnly = true)
    open override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val searchSession = Search.session(entityManager)

        val indexer = searchSession.massIndexer(ResourceEntity::class.java)
            .threadsToLoadObjects(7)

        indexer.startAndWait()
    }

    @Transactional(readOnly = true)
    open fun search(query: String): List<ResourceEntity> {
        val queryString = query
            .replace("\t", " ")
            .replace(specialChars, "")
            .split(" ")
            .joinToString("|") { when {
                it.length < 3 -> it
                it.length < 5 -> "${it}~1"
                it.length < 8 -> "${it}~2"
                else -> "${it}~3"
            }}

        println(queryString)

//            .onFields("name", "ownerGroup.name", "maintainerDivisions.name")
//            .matching(queryString)
//            .filter { it.visible }

        return Search.session(entityManager)
            .search(ResourceEntity::class.java)
            .where { it.simpleQueryString()
                .fields("name", "ownerGroup.name", "maintainerDivisions.name")
                .matching(queryString)
            }
            .fetchHits(20)
            .filterIsInstance(ResourceEntity::class.java)
    }

}
