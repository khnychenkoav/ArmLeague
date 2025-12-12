package com.armleague.backend.dto

import com.armleague.backend.model.Gender
import com.armleague.backend.model.HandType
import java.math.BigDecimal

data class CreateWeightClassDto(
    val className: String,
    val maxWeightKg: BigDecimal,
    val gender: Gender,
    val hand: HandType,
    val entryFee: BigDecimal
)

data class CreateRegistrationDto(
    val tournamentId: Int,
    val athleteId: Int,
    val weightClassId: Int
)