package com.armleague.backend.dto

import com.armleague.backend.model.Gender
import com.armleague.backend.model.HandType
import java.math.BigDecimal

data class UpdateWeightClassDto(
    val className: String? = null,
    val maxWeightKg: BigDecimal? = null,
    val gender: Gender? = null,
    val hand: HandType? = null,
    val entryFee: BigDecimal? = null
)