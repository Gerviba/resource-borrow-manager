package hu.gerviba.borrower.config

import hu.gerviba.borrower.model.*
import hu.gerviba.borrower.repo.*
import hu.gerviba.borrower.service.QRCodeGeneratorService
import hu.gerviba.borrower.service.TimeService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Profile("test")
@Service
class TestingConfig(
    val groupRepository: GroupRepository,
    val divisionRepository: DivisionRepository,
    val resourceRepository: ResourceRepository,
    val userRepository: UserRepository,
    val bookingRepository: BookingRepository,
    val clock: TimeService,
    val qrGenerator: QRCodeGeneratorService
) {

    @PostConstruct
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun init() {
        val group1 = GroupEntity(name = "AUT", longName = "Automatizálási és Alkalmazott Informatikai Tanszék", visible = true)
        groupRepository.save(group1)
        val group2 = GroupEntity(name = "VET", longName = "Villamos Energetika Tanszék", visible = true)
        groupRepository.save(group2)
        val group3 = GroupEntity(name = "AITT", longName = "Aktuális Informatikát Tanító Tanszék", visible = true)
        groupRepository.save(group3)

        val div1 = DivisionEntity(name = "g1_div1", parentGroup = group1, visible = true)
        divisionRepository.save(div1)
        val div2 = DivisionEntity(name = "g1_div2", parentGroup = group1, visible = true)
        divisionRepository.save(div2)
        val div3 = DivisionEntity(name = "nagyfesz labor", parentGroup = group2, visible = true)
        divisionRepository.save(div3)
        val div4 = DivisionEntity(name = "kisfesz labor", parentGroup = group2, visible = true)
        divisionRepository.save(div4)

        val res1 = ResourceEntity(
            name = "villám",
            ownerGroup = group2,
            code = "C1",
            visible = true,
            available = true,
            created = clock.now(),
            lastChecked = clock.now(),
            lastUpdated = clock.now()
        )
        res1.maintainerDivisions.add(div3)
        resourceRepository.save(res1)
        qrGenerator.generateForResource(res1)

        val res2 = ResourceEntity(
            name = "amperok", ownerGroup = group2, code = "C2", visible = true,
            created = clock.now(),
            lastChecked = clock.now(),
            lastUpdated = clock.now()
        )
        res2.maintainerDivisions.add(div3)
        resourceRepository.save(res2)
        qrGenerator.generateForResource(res2)

        val res3 = ResourceEntity(
            name = "voltok", ownerGroup = group2, code = "C3", visible = true,
            description = "Lorem ipsum dolor sit amet lorem ipsum dolor sit amet.",
            storageRoom = "I épület, IB420", available = true,
            created = clock.now(),
            lastChecked = clock.now(),
            lastUpdated = clock.now()
        )
        res3.maintainerDivisions.add(div3)
        resourceRepository.save(res3)
        qrGenerator.generateForResource(res3)

        val user1 = UserEntity(name = "User 1", email = "test@email.com", room = "SCH-1920", locked = false, teams = "teams.addr@ms.email.com", neptun = "BATMAN")
        user1.groups.add(group2)
        user1.divisions.add(div3)
        userRepository.save(user1)

        val user2 = UserEntity(name = "User 2")
        userRepository.save(user2)

        val user3 = UserEntity(name = "User 3")
        userRepository.save(user3)

        val book1 = BookingEntity(
            resource = res3, borrower = user1,
            dateStart = System.currentTimeMillis() - 30000, dateEnd = System.currentTimeMillis() + 30 * 60 * 1000,
            accepted = true, issued = true
        )
        bookingRepository.save(book1)

        val book2 = BookingEntity(
            resource = res3, borrower = user1,
            dateStart = System.currentTimeMillis() + 60 * 60 * 1000, dateEnd = System.currentTimeMillis() + 90 * 60 * 1000,
            accepted = true
        )
        bookingRepository.save(book2)

        val book3 = BookingEntity(
            resource = res3,
            borrower = user2,
            dateStart = System.currentTimeMillis() + 530 * 60 * 1000,
            dateEnd = System.currentTimeMillis() + 540 * 60 * 1000,
            accepted = true
        )
        bookingRepository.save(book3)

        bookingRepository.save(
            BookingEntity(
                resource = res3,
                borrower = user3,
                dateStart = System.currentTimeMillis() + 1530 * 60 * 1000,
                dateEnd = System.currentTimeMillis() + 1540 * 60 * 1000,
                accepted = false,
                borrowerComment = "Ez a megjegyzésem"
            )
        )

        bookingRepository.save(
            BookingEntity(
                resource = res3,
                borrower = null,
                dateStart = System.currentTimeMillis() + 1730 * 60 * 1000,
                dateEnd = System.currentTimeMillis() + 1740 * 60 * 1000,
                accepted = false,
                rejected = true
            )
        )

        resourceRepository.save(ResourceEntity(name = "automatizálás", ownerGroup = group1, code = "C4", visible = true))
        resourceRepository.save(ResourceEntity(name = "alkalmazás", ownerGroup = group1, code = "C5", visible = true))
        resourceRepository.save(ResourceEntity(name = "informatika", ownerGroup = group1, code = "C6", visible = true))
        resourceRepository.save(ResourceEntity(name = "nem látható", ownerGroup = group1, code = "C7", visible = false))

    }

}
