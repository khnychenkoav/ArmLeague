package com.armleague.backend.service

import com.armleague.backend.dto.AthleteProfileDto
import com.armleague.backend.dto.RegisterAthleteDto
import com.armleague.backend.dto.UpdateAthleteDto
import com.armleague.backend.model.Athlete
import com.armleague.backend.model.User
import com.armleague.backend.repository.AthleteRepository
import com.armleague.backend.repository.MatchRepository
import com.armleague.backend.repository.RankingRepository
import com.armleague.backend.repository.RegistrationRepository
import com.armleague.backend.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AthleteService(
    private val athleteRepository: AthleteRepository,
    private val userRepository: UserRepository,
    private val matchRepository: MatchRepository,
    private val registrationRepository: RegistrationRepository,
    private val rankingRepository: RankingRepository,
) {

    fun getAllAthletes(): List<Athlete> = athleteRepository.findAll()

    @Transactional
    fun registerNewAthlete(dto: RegisterAthleteDto): Athlete {
        if (userRepository.existsByEmail(dto.email)) {
            throw RuntimeException("Email already taken")
        }

        val newUser = User(
            email = dto.email,
            passwordHash = User.hashPassword(dto.password),
            fullName = dto.fullName,
            phoneNumber = dto.phoneNumber
        )
        val savedUser = userRepository.save(newUser)

        val newAthlete = Athlete(
            user = savedUser,
            nickname = dto.nickname,
            countryCode = dto.countryCode,
            city = dto.city,
            birthDate = dto.birthDate,
            gender = dto.gender,
            heightCm = dto.heightCm,
            weightKg = dto.weightKg,
            bicepsCm = dto.bicepsCm,
            forearmCm = dto.forearmCm,
            bio = dto.bio
        )

        return athleteRepository.save(newAthlete)
    }

    fun getAthleteById(id: Int): Athlete {
        return athleteRepository.findById(id).orElseThrow {
            RuntimeException("Athlete not found with id: $id")
        }
    }

    @Transactional
    fun updateAthlete(id: Int, dto: UpdateAthleteDto): Athlete {
        val athlete = getAthleteById(id)
        val user = athlete.user

        dto.fullName?.let { user.fullName = it }
        dto.phoneNumber?.let { user.phoneNumber = it }
        userRepository.save(user)

        dto.nickname?.let { athlete.nickname = it }
        dto.city?.let { athlete.city = it }
        dto.heightCm?.let { athlete.heightCm = it }
        dto.weightKg?.let { athlete.weightKg = it }
        dto.bicepsCm?.let { athlete.bicepsCm = it }
        dto.forearmCm?.let { athlete.forearmCm = it }
        dto.bio?.let { athlete.bio = it }

        return athleteRepository.save(athlete)
    }

    @Transactional
    fun deleteAthlete(id: Int) {
        val athlete = getAthleteById(id)

        val matchesAsA = matchRepository.findByAthleteA(athlete)
        val matchesAsB = matchRepository.findByAthleteB(athlete)
        matchRepository.deleteAll(matchesAsA)
        matchRepository.deleteAll(matchesAsB)

        val registrations = registrationRepository.findByAthlete(athlete)
        registrationRepository.deleteAll(registrations)

        val rankings = rankingRepository.findByAthlete(athlete)
        rankingRepository.deleteAll(rankings)

        athleteRepository.delete(athlete)

        userRepository.deleteById(athlete.user.id!!)
    }

    fun getAthleteProfile(id: Int): AthleteProfileDto {
        val athlete = getAthleteById(id)
        val rankings = rankingRepository.findByAthleteId(id)
        val matches = matchRepository.findByAthleteA_IdOrAthleteB_Id(id, id)

        return AthleteProfileDto(
            athlete = athlete,
            rankings = rankings,
            matchHistory = matches
        )
    }
}