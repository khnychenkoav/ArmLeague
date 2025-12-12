package com.armleague.backend.service

import com.armleague.backend.dto.CreateRefereeDto
import com.armleague.backend.dto.UpdateRefereeDto
import com.armleague.backend.model.Referee
import com.armleague.backend.model.User
import com.armleague.backend.model.UserRole
import com.armleague.backend.repository.RefereeRepository
import com.armleague.backend.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RefereeService(
    private val refereeRepository: RefereeRepository,
    private val userRepository: UserRepository
) {

    fun getAllReferees(): List<Referee> = refereeRepository.findAll()

    fun getRefereeById(id: Int): Referee {
        return refereeRepository.findById(id).orElseThrow {
            RuntimeException("Referee not found with id: $id")
        }
    }

    @Transactional
    fun createReferee(dto: CreateRefereeDto): Referee {
        if (userRepository.existsByEmail(dto.email)) {
            throw RuntimeException("Email already taken")
        }

        val newUser = User(
            email = dto.email,
            passwordHash = User.hashPassword(dto.password),
            fullName = dto.fullName,
            role = UserRole.REFEREE
        )
        val savedUser = userRepository.save(newUser)

        val newReferee = Referee(
            user = savedUser,
            certificationLevel = dto.certificationLevel ?: "National",
            licenseNumber = dto.licenseNumber,
            yearsExperience = dto.yearsExperience ?: 0
        )
        return refereeRepository.save(newReferee)
    }

    @Transactional
    fun updateReferee(id: Int, dto: UpdateRefereeDto): Referee {
        val referee = getRefereeById(id)
        val user = referee.user

        dto.fullName?.let { user.fullName = it }
        userRepository.save(user)

        dto.certificationLevel?.let { referee.certificationLevel = it }
        dto.licenseNumber?.let { referee.licenseNumber = it }
        dto.yearsExperience?.let { referee.yearsExperience = it }
        dto.isActive?.let { referee.isActive = it }

        return refereeRepository.save(referee)
    }

    @Transactional
    fun deleteReferee(id: Int) {
        val referee = getRefereeById(id)
        userRepository.deleteById(referee.user.id!!)
    }
}