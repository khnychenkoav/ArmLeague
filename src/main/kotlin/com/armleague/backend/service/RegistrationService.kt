package com.armleague.backend.service

import com.armleague.backend.dto.CreateRegistrationDto
import com.armleague.backend.dto.CreateWeightClassDto
import com.armleague.backend.dto.UpdateRegistrationDto
import com.armleague.backend.dto.UpdateWeightClassDto
import com.armleague.backend.dto.WeighInDto
import com.armleague.backend.model.RegistrationStatus
import com.armleague.backend.model.TournamentRegistration
import com.armleague.backend.model.WeightClass
import com.armleague.backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class RegistrationService(
    private val tournamentRepository: TournamentRepository,
    private val weightClassRepository: WeightClassRepository,
    private val athleteRepository: AthleteRepository,
    private val registrationRepository: RegistrationRepository
) {

    fun addWeightClass(tournamentId: Int, dto: CreateWeightClassDto): WeightClass {
        val tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow { RuntimeException("Tournament not found") }

        val weightClass = WeightClass(
            tournament = tournament,
            className = dto.className,
            maxWeightKg = dto.maxWeightKg,
            gender = dto.gender,
            hand = dto.hand,
            entryFee = dto.entryFee
        )
        return weightClassRepository.save(weightClass)
    }

    fun getClassesByTournament(tournamentId: Int): List<WeightClass> {
        return weightClassRepository.findByTournamentId(tournamentId)
    }

    @Transactional
    fun registerAthlete(dto: CreateRegistrationDto): TournamentRegistration {
        val tournament = tournamentRepository.findById(dto.tournamentId)
            .orElseThrow { RuntimeException("Tournament not found") }

        val athlete = athleteRepository.findById(dto.athleteId)
            .orElseThrow { RuntimeException("Athlete not found") }

        val weightClass = weightClassRepository.findById(dto.weightClassId)
            .orElseThrow { RuntimeException("Weight Class not found") }

        if (registrationRepository.existsByAthleteIdAndWeightClassId(dto.athleteId, dto.weightClassId)) {
            throw RuntimeException("Athlete already registered in this category")
        }

        val registration = TournamentRegistration(
            tournament = tournament,
            athlete = athlete,
            weightClass = weightClass
        )

        return registrationRepository.save(registration)
    }

    fun getRegistrations(tournamentId: Int): List<TournamentRegistration> {
        return registrationRepository.findByTournamentId(tournamentId)
    }

    fun getRegistrationById(id: Int): TournamentRegistration {
        return registrationRepository.findById(id).orElseThrow {
            RuntimeException("Registration not found with id: $id")
        }
    }

    @Transactional
    fun updateRegistration(id: Int, dto: UpdateRegistrationDto): TournamentRegistration {
        val registration = getRegistrationById(id)

        dto.weightClassId?.let { newClassId ->
            val newClass = weightClassRepository.findById(newClassId)
                .orElseThrow { RuntimeException("Weight Class not found") }
            registration.weightClass = newClass
        }

        dto.weighInWeight?.let { registration.weighInWeight = it }
        dto.status?.let { registration.status = it }
        dto.isPaid?.let { registration.isPaid = it }

        return registrationRepository.save(registration)
    }

    fun deleteRegistration(id: Int) {
        if (registrationRepository.existsById(id)) {
            registrationRepository.deleteById(id)
        } else {
            throw RuntimeException("Registration not found")
        }
    }

    fun getWeightClassById(id: Int): WeightClass {
        return weightClassRepository.findById(id).orElseThrow {
            RuntimeException("Weight class not found with id: $id")
        }
    }

    @Transactional
    fun updateWeightClass(id: Int, dto: UpdateWeightClassDto): WeightClass {
        val weightClass = getWeightClassById(id)

        dto.className?.let { weightClass.className = it }
        dto.maxWeightKg?.let { weightClass.maxWeightKg = it }
        dto.gender?.let { weightClass.gender = it }
        dto.hand?.let { weightClass.hand = it }
        dto.entryFee?.let { weightClass.entryFee = it }

        return weightClassRepository.save(weightClass)
    }

    fun deleteWeightClass(id: Int) {
        if (weightClassRepository.existsById(id)) {
            weightClassRepository.deleteById(id)
        } else {
            throw RuntimeException("Weight class not found")
        }
    }

    @Transactional
    fun batchCreateWeightClasses(
        tournamentId: Int,
        dtos: List<CreateWeightClassDto>,
        dryRun: Boolean
    ): Map<String, Any> {
        val tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow { RuntimeException("Tournament not found") }

        var successCount = 0
        val errors = mutableListOf<String>()

        dtos.forEachIndexed { index, dto ->
            try {
                if (dto.className.isBlank()) throw IllegalArgumentException("Class name is blank")
                if (dto.maxWeightKg <= BigDecimal.ZERO) throw IllegalArgumentException("Max weight must be positive")

                if (!dryRun) {
                    val weightClass = WeightClass(
                        tournament = tournament,
                        className = dto.className,
                        maxWeightKg = dto.maxWeightKg,
                        gender = dto.gender,
                        hand = dto.hand,
                        entryFee = dto.entryFee
                    )
                    weightClassRepository.save(weightClass)
                }
                successCount++
            } catch (e: Exception) {
                errors.add("Error at index $index: ${e.message}")
            }
        }
        return mapOf("total" to dtos.size, "processed" to successCount, "errors" to errors, "dryRun" to dryRun)
    }

    @Transactional
    fun performWeighIn(registrationId: Int, dto: WeighInDto): TournamentRegistration {
        val registration = getRegistrationById(registrationId)
        val athlete = registration.athlete

        if (dto.weight > registration.weightClass!!.maxWeightKg) {
            registration.status = RegistrationStatus.DISQUALIFIED
            throw RuntimeException("Athlete weight (${dto.weight}) exceeds class limit (${registration.weightClass!!.maxWeightKg})")
        }

        registration.weighInWeight = dto.weight
        registration.status = RegistrationStatus.APPROVED
        registration.isPaid = true

        athlete.weightKg = dto.weight
        athleteRepository.save(athlete)

        return registrationRepository.save(registration)
    }
}