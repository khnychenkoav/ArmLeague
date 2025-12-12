package com.armleague.backend.repository

import com.armleague.backend.model.Athlete
import com.armleague.backend.model.Ranking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RankingRepository : JpaRepository<Ranking, Int> {
    fun findByAthlete(athlete: Athlete): List<Ranking>
    fun findByAthleteId(athleteId: Int): List<Ranking>
}