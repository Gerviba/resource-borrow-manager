package hu.gerviba.borrower.service

import hu.gerviba.borrower.dto.BookingEntityDto
import hu.gerviba.borrower.dto.BorrowRequestDto
import hu.gerviba.borrower.exception.BusinessOperationFailedException
import hu.gerviba.borrower.model.BookingEntity
import hu.gerviba.borrower.model.DivisionEntity
import hu.gerviba.borrower.model.ResourceEntity
import hu.gerviba.borrower.model.UserEntity
import hu.gerviba.borrower.repo.BookingRepository
import hu.gerviba.borrower.repo.DivisionRepository
import hu.gerviba.borrower.repo.GroupRepository
import hu.gerviba.borrower.repo.ResourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
open class ResourceService(
    private val resourceRepository: ResourceRepository,
    private val divisionRepository: DivisionRepository,
    private val groupRepository: GroupRepository,
    private val bookingRepository: BookingRepository,
    private val clock: TimeService,
    private val qrGenerator: QRCodeGeneratorService,
) {

    @Transactional(readOnly = false, isolation = Isolation.READ_UNCOMMITTED)
    open fun createResource(dto: ResourceEntity, groupId: Int) {
        val resource = createEntityWithDefensiveCopy(dto, groupId)
        resourceRepository.save(resource)
        qrGenerator.generateForResource(resource)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun createResourceAndAttach(dto: ResourceEntity, groupId: Int, divisionId: Int) {
        val entity = createEntityWithDefensiveCopy(dto, groupId)
        entity.maintainerDivisions.add(divisionRepository.getById(divisionId))
        resourceRepository.save(entity)
        qrGenerator.generateForResource(entity)
    }

    private fun createEntityWithDefensiveCopy(
        dto: ResourceEntity,
        groupId: Int
    ) = ResourceEntity(
        name = dto.name,
        code = dto.code,
        description = dto.description,
        visible = dto.visible,
        available = dto.available,
        storageRoom = dto.storageRoom,
        ownerGroup = groupRepository.getById(groupId),
        lastUpdated = clock.now(),
        created = clock.now(),
        lastChecked = clock.now(),
        imageName = dto.imageName
    )

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllOfGroup(groupId: Int): List<ResourceEntity> {
        return resourceRepository.findAllByOwnerGroup_Id(groupId)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getResource(resourceId: Int): ResourceEntity {
        return resourceRepository.getById(resourceId).copy()
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun updateResource(divisionId: Int, resource: ResourceEntity) {
        val resourceEntity = resourceRepository.getById(divisionId).apply {
            name = resource.name
            code = resource.code
            description = resource.description
            visible = resource.visible
            available = resource.available
            storageRoom = resource.storageRoom
            lastUpdated = clock.now()
            imageName = resource.imageName
        }
        resourceRepository.save(resourceEntity)
        qrGenerator.generateForResource(resourceEntity)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun grantAccessToDivision(resourceId: Int, divisionId: Int) {
        resourceRepository.save(resourceRepository.getById(resourceId).apply {
            maintainerDivisions.add(divisionRepository.getById(divisionId))
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun revokeAccessFromDivision(resourceId: Int, divisionId: Int) {
        resourceRepository.save(resourceRepository.getById(resourceId).apply {
            maintainerDivisions.remove(divisionRepository.getById(divisionId))
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getMaintainersOfResource(resourceId: Int): MutableSet<DivisionEntity> {
        return resourceRepository.getById(resourceId).maintainerDivisions
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllForDivision(divisionId: Int): List<ResourceEntity> {
        return resourceRepository.findAllByMaintainerDivisionsId(divisionId)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getBookingsWithingRange(resourceId: Int, from: Long, to: Long): List<BookingEntityDto> {
        return bookingRepository.findAllByResourceInInterval(resourceId, from, to)
            .map { BookingEntityDto(it) }
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun borrowResource(resourceId: Int, borrowRequest: BorrowRequestDto, user: UserEntity) {
        val resource = resourceRepository.getByIdAndVisibleTrue(resourceId)
        if (borrowRequest.dateEnd <= borrowRequest.dateStart) {
            throw BusinessOperationFailedException("Invalid range")
        }
        if (getBookingsWithingRange(resourceId, borrowRequest.dateStart * 1000, borrowRequest.dateEnd * 1000).isNotEmpty()) {
            throw BusinessOperationFailedException("Resource is used in this period")
        }

        bookingRepository.save(
            BookingEntity(
                resource = resource,
                borrower = user,
                handlerAdministerInbound = null,
                handlerAdministerOutbound = null,
                dateStart = borrowRequest.dateStart * 1000,
                dateEnd = borrowRequest.dateEnd * 1000,
                realDateStart = 0,
                realDateEnd = 0,
                accepted = false,
                issued = false,
                closed = false,
                borrowerComment = borrowRequest.comment
            )
        )
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getBookingsFromNow(resourceId: Int): List<BookingEntityDto> {
        return bookingRepository.findAllByResource_IdAndDateEndGreaterThanAndAcceptedTrue(resourceId, clock.now())
            .map { BookingEntityDto(it) }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun existsByCode(code: String): Boolean {
        return resourceRepository.existsByCode(code)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAcceptedBookings(resourceId: Int): List<BookingEntity> {
        return bookingRepository.findAllByResource_IdAndAcceptedTrue(resourceId)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getBookingsHandledByUser(user: UserEntity): List<BookingEntity> {
        return user.divisions
            .flatMap { resourceRepository.findAllByMaintainerDivisionsContains(it) }
            .distinct()
            .flatMap { it.bookings }
            .sortedBy { it.dateStart }
    }

    fun getNotAcceptedBookingsHandledByUserNotTransactional(user: UserEntity) =
        getBookingsHandledByUser(user)
            .filter { !it.accepted && !it.rejected }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun updateBookingIfExists(requestId: Int, update: (BookingEntity) -> Unit) {
        bookingRepository.findById(requestId).ifPresent(update)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getBooking(requestId: Int): BookingEntity {
        return bookingRepository.getById(requestId)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getBookingsBorrowedByUser(user: UserEntity): List<BookingEntity> {
        return bookingRepository.findAllByBorrowerOrderByDateStart(user)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getResourceByCode(code: String): Optional<ResourceEntity> {
        return resourceRepository.findByCodeAndVisibleTrue(code)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun receiveResource(user: UserEntity, code: String): Optional<ResourceEntity> {
        return resourceRepository.findByCodeAndVisibleTrueAndAvailableTrue(code).filter { resource ->
            val bookings = bookingRepository.findAllByBorrower_IdAndResource_IdAndAcceptedTrueAndRejectedFalseAndIssuedFalseAndClosedFalse(
                user.id,
                resource.id
            )
            bookings.forEach { booking ->
                booking.issued = true
                booking.realDateStart = clock.now()
                bookingRepository.save(booking)
            }
            return@filter bookings.isNotEmpty()
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun returnResource(user: UserEntity, code: String): Optional<Pair<ResourceEntity, BookingEntity>> {
        val resource = resourceRepository.findByCodeAndVisibleTrueAndAvailableTrue(code)
        if (resource.isEmpty)
            return Optional.empty()

        val bookings = bookingRepository.findAllByResource_IdAndIssuedTrueAndClosedFalse(resource.orElseThrow().id)
            .filter { booking -> getBookingsHandledByUser(user).any { booking.id == it.id } }

        bookings.forEach { booking ->
            booking.closed = true
            booking.realDateEnd = clock.now()
            booking.handlerAdministerInbound = user
            bookingRepository.save(booking)
        }

        if (bookings.isEmpty())
            return Optional.empty()

        return Optional.of(Pair(resource.orElseThrow(), bookings.first()))
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun addCommentIfApplicable(user: UserEntity, bookingId: Int, comment: String) {
        bookingRepository.findById(bookingId).ifPresent { booking ->
            if (booking.handlerAdministerInbound?.id == user.id && booking.recaptureComment.isBlank()) {
                booking.recaptureComment = comment
                bookingRepository.save(booking)
            }
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun updateInventoryCheck(code: String): String {
        return resourceRepository.findByCode(code)
            .map {
                it.lastChecked = clock.now()
                resourceRepository.save(it)
                return@map it
            }
            .map { it.name }
            .orElse("NEM TALÁLHATÓ")
    }

}
