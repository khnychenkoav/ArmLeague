package com.armleague.backend.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

enum class TournamentStatus {
    ANNOUNCED, REGISTRATION, ONGOING, FINISHED, CANCELED
}

@Entity
@Table(name = "tournaments")
data class Tournament(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tournament_id")
    val id: Int? = null,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var country: String,

    @Column(nullable = false)
    var location: String,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDateTime,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDateTime,

    @Column(name = "prize_pool")
    var prizePool: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: TournamentStatus = TournamentStatus.ANNOUNCED,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
)