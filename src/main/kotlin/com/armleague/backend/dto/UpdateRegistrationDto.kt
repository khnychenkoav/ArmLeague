package com.armleague.backend.dto

import com.armleague.backend.model.RegistrationStatus
import java.math.BigDecimal

data class UpdateRegistrationDto(
    val weightClassId: Int? = null,
    val weighInWeight: BigDecimal? = null,
    val status: RegistrationStatus? = null,
    val isPaid: Boolean? = null
)