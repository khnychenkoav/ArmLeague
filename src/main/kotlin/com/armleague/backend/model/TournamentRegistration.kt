package com.armleague.backend.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

enum class RegistrationStatus {
    PENDING, APPROVED, REJECTED, DISQUALIFIED
}

@Entity
@Table(name = "tournament_registrations")
data class TournamentRegistration(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    val tournament: Tournament,

    @ManyToOne
    @JoinColumn(name = "athlete_id", nullable = false)
    val athlete: Athlete,

    @ManyToOne
    @JoinColumn(name = "class_id")
    var weightClass: WeightClass? = null,

    @Column(name = "weigh_in_weight")
    var weighInWeight: BigDecimal? = null,

    @Enumerated(EnumType.STRING)
    var status: RegistrationStatus = RegistrationStatus.PENDING,

    @Column(name = "is_paid")
    var isPaid: Boolean = false,

    @Column(name = "registered_at")
    var registeredAt: LocalDateTime = LocalDateTime.now()
)