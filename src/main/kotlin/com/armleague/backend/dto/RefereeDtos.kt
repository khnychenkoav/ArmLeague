package com.armleague.backend.dto

data class CreateRefereeDto(
    val email: String,
    val password: String,
    val fullName: String,
    val certificationLevel: String?,
    val licenseNumber: String?,
    val yearsExperience: Int?
)

data class UpdateRefereeDto(
    val fullName: String? = null,
    val certificationLevel: String? = null,
    val licenseNumber: String? = null,
    val yearsExperience: Int? = null,
    val isActive: Boolean? = null
)