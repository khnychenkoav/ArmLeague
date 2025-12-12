package com.armleague.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import org.hibernate.annotations.Immutable
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Immutable
@Table(name = "v_athlete_summary")
data class AthleteSummaryView(
    @Id
    @Column(name = "athlete_id")
    val id: Int,

    @Column(name = "full_name")
    val fullName: String,

    @Column(name = "country_code")
    val countryCode: String,

    val hand: String?,
    val points: Int?,
    val wins: Int?,
    val losses: Int?,

    @Column(name = "win_rate")
    val winRate: BigDecimal?
)

@Entity
@Immutable
@Table(name = "v_match_schedule")
data class MatchScheduleView(
    @Id
    @Column(name = "match_id")
    val id: Int,

    @Column(name = "tournament_name")
    val tournamentName: String,

    @Column(name = "class_name")
    val className: String,

    val stage: String,

    @Column(name = "athlete_a")
    val athleteA: String,

    @Column(name = "athlete_b")
    val athleteB: String,

    @Column(name = "start_time")
    val startTime: LocalDateTime?,

    @Column(name = "table_number")
    val tableNumber: Int?
)

@Entity
@Immutable
@Table(name = "v_referee_workload")
data class RefereeWorkloadView(
    @Id
    @Column(name = "referee_id")
    val id: Int,

    @Column(name = "full_name")
    val fullName: String,

    @Column(name = "matches_judged")
    val matchesJudged: Long,

    @Column(name = "total_fouls_called")
    val totalFoulsCalled: Long?
)