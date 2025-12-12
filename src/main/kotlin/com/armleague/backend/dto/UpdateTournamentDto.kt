package com.armleague.backend.dto

import com.armleague.backend.model.TournamentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class UpdateTournamentDto(
    val name: String? = null,
    val description: String? = null,
    val country: String? = null,
    val location: String? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val prizePool: BigDecimal? = null,
    val status: TournamentStatus? = null
)