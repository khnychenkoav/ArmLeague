package com.armleague.backend.repository

import com.armleague.backend.model.WeightClass
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WeightClassRepository : JpaRepository<WeightClass, Int> {
    fun findByTournamentId(tournamentId: Int): List<WeightClass>
}