package com.armleague.backend.dto

import java.math.BigDecimal

data class UpdateAthleteDto(
    val fullName: String? = null,
    val phoneNumber: String? = null,

    val nickname: String? = null,
    val city: String? = null,
    val heightCm: Int? = null,
    val weightKg: BigDecimal? = null,
    val bicepsCm: BigDecimal? = null,
    val forearmCm: BigDecimal? = null,
    val bio: String? = null
)