package com.armleague.backend.service

import com.armleague.backend.model.Ranking
import com.armleague.backend.repository.RankingRepository
import org.springframework.stereotype.Service

@Service
class RankingService(
    private val repository: RankingRepository
) {

    fun getAllRankings(): List<Ranking> {
        return repository.findAll().sortedByDescending { it.points }
    }

    fun getRankingsByAthlete(athleteId: Int): List<Ranking> {
        return repository.findByAthleteId(athleteId)
    }
}