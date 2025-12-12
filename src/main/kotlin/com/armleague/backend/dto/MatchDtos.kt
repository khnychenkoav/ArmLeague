package com.armleague.backend.dto

import com.armleague.backend.model.HandType
import com.armleague.backend.model.MatchResultType
import java.time.LocalDateTime

data class CreateMatchDto(
    val tournamentId: Int,
    val weightClassId: Int,
    val athleteAId: Int,
    val athleteBId: Int,
    val stage: String,
    val tableNumber: Int?,
    val matchNumber: Int?,
    val startTime: LocalDateTime?
)

data class UpdateMatchDto(
    val stage: String? = null,
    val tableNumber: Int? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val winnerId: Int? = null,
    val athleteAId: Int? = null,
    val athleteBId: Int? = null
)

data class SetMatchResultDto(
    val winnerId: Int,
    val resultType: MatchResultType,
    val winningHand: HandType,
    val durationSeconds: Int?,
    val foulsA: Int = 0,
    val foulsB: Int = 0,
    val isStrapMatch: Boolean = false,
    val notes: String? = null
)