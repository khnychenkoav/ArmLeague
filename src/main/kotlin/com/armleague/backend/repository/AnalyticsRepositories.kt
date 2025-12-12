package com.armleague.backend.repository

import com.armleague.backend.model.AthleteSummaryView
import com.armleague.backend.model.MatchScheduleView
import com.armleague.backend.model.RefereeWorkloadView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface AthleteSummaryRepository : JpaRepository<AthleteSummaryView, Int>

@Repository
interface MatchScheduleRepository : JpaRepository<MatchScheduleView, Int>

@Repository
interface RefereeWorkloadRepository : JpaRepository<RefereeWorkloadView, Int>

interface AthleteHistoryProjection {
    fun getMatchDate(): java.time.LocalDateTime
    fun getOpponentName(): String
    fun getResult(): String
    fun getStage(): String
}

@Repository
interface AthleteFunctionsRepository : JpaRepository<com.armleague.backend.model.Athlete, Int> {

    @Query(value = "SELECT calculate_bmi(:weight, :height)", nativeQuery = true)
    fun calculateBmi(weight: BigDecimal, height: Int): BigDecimal

    @Query(value = "SELECT * FROM get_athlete_history(:athleteId)", nativeQuery = true)
    fun getAthleteHistory(athleteId: Int): List<AthleteHistoryProjection>
}