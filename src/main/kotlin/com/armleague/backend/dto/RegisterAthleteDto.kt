package com.armleague.backend.dto

import com.armleague.backend.model.Gender
import java.math.BigDecimal
import java.time.LocalDate

data class RegisterAthleteDto(
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String?,

    val nickname: String?,
    val countryCode: String,
    val city: String?,
    val birthDate: LocalDate,
    val gender: Gender,
    val heightCm: Int?,
    val weightKg: BigDecimal?,

    val bicepsCm: BigDecimal?,
    val forearmCm: BigDecimal?,
    val bio: String?
)