package hu.gerviba.borrower.service

import hu.gerviba.borrower.model.UserEntity
import hu.gerviba.borrower.repo.DivisionRepository
import hu.gerviba.borrower.repo.GroupRepository
import hu.gerviba.borrower.repo.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
open class UserService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val divisionRepository: DivisionRepository
) {

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllFromDivision(divisionId: Int): List<UserEntity> {
        return userRepository.findAllByDivisionsId(divisionId)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllFromGroup(groupId: Int): List<UserEntity> {
        return userRepository.findAllByGroupsId(groupId)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun addUserToGroup(userId: Int, groupId: Int) {
        userRepository.save(userRepository.getById(userId).apply {
            groups.add(groupRepository.getById(groupId))
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun kickUserOutOfGroup(userId: Int, groupId: Int) {
        userRepository.save(userRepository.getById(userId).apply {
            groups.removeIf { it.id == groupId }
        })
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getAllUsers(): List<UserEntity> {
        return userRepository.findAll()
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    open fun getUser(userId: Int): UserEntity {
        return userRepository.getById(userId)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun updateUserByAdmin(userId: Int, dto: UserEntity) {
        userRepository.save(userRepository.getById(userId).apply {
            name = dto.name
            role = dto.role
            permissions = dto.permissions
            locked = dto.locked
            updateRegularUserDetails(dto)
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun updateUser(userId: Int, dto: UserEntity) {
        userRepository.save(userRepository.getById(userId).apply {
            updateRegularUserDetails(dto)
        })
    }

    private fun UserEntity.updateRegularUserDetails(dto: UserEntity) {
        email = dto.email
        mobile = dto.mobile
        room = dto.room
        teams = dto.teams
        neptun = dto.neptun
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun addUserToDivision(userId: Int, divisionId: Int) {
        userRepository.save(userRepository.getById(userId).apply {
            divisions.add(divisionRepository.getById(divisionId))
        })
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun kickUserOutOfDivision(userId: Int, divisionId: Int) {
        userRepository.save(userRepository.getById(userId).apply {
            divisions.removeIf { it.id == divisionId }
        })
    }

}
