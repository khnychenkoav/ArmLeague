package com.armleague.backend.repository

import com.armleague.backend.model.Athlete
import com.armleague.backend.model.TournamentRegistration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistrationRepository : JpaRepository<TournamentRegistration, Int> {
    fun findByTournamentId(tournamentId: Int): List<TournamentRegistration>

    fun existsByAthleteIdAndWeightClassId(athleteId: Int, classId: Int): Boolean

    fun findByAthlete(athlete: Athlete): List<TournamentRegistration>
}