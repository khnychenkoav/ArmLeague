package com.armleague.backend.repository

import com.armleague.backend.model.Athlete
import com.armleague.backend.model.Match
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : JpaRepository<Match, Int> {
    fun findByTournamentId(tournamentId: Int): List<Match>

    fun findByWeightClassId(classId: Int): List<Match>

    fun findByAthleteA(athlete: Athlete): List<Match>

    fun findByAthleteB(athlete: Athlete): List<Match>

    fun findByAthleteA_IdOrAthleteB_Id(id1: Int, id2: Int): List<Match>
}