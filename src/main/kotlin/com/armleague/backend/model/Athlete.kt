package com.armleague.backend.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "athletes")
data class Athlete(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "athlete_id")
    val id: Int? = null,


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    val user: User,

    var nickname: String? = null,

    @Column(name = "country_code", nullable = false)
    var countryCode: String,

    var city: String? = null,

    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate,

    @Enumerated(EnumType.STRING)
    var gender: Gender,

    @Column(name = "height_cm")
    var heightCm: Int? = null,

    @Column(name = "weight_kg")
    var weightKg: BigDecimal? = null,

    @Column(name = "biceps_cm")
    var bicepsCm: BigDecimal? = null,

    @Column(name = "forearm_cm")
    var forearmCm: BigDecimal? = null,

    var bio: String? = null
)