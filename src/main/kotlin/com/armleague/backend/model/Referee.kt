package com.armleague.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "referees")
data class Referee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referee_id")
    val id: Int? = null,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    val user: User,

    @Column(name = "certification_level")
    var certificationLevel: String = "National",

    @Column(name = "license_number", unique = true)
    var licenseNumber: String? = null,

    @Column(name = "years_experience")
    var yearsExperience: Int = 0,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)