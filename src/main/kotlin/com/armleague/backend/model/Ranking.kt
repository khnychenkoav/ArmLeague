package com.armleague.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "rankings")
data class Ranking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ranking_id")
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    val athlete: Athlete,

    @Enumerated(EnumType.STRING)
    var hand: HandType,

    var points: Int = 1000,

    var wins: Int = 0,

    var losses: Int = 0,

    @Column(name = "tournaments_played")
    var tournamentsPlayed: Int = 0,

    @Column(name = "last_updated")
    var lastUpdated: LocalDateTime = LocalDateTime.now()
)