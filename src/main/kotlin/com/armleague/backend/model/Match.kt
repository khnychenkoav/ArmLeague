package com.armleague.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "matches")
data class Match(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    val tournament: Tournament,

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    val weightClass: WeightClass,

    @ManyToOne
    @JoinColumn(name = "athlete_a_id", nullable = false)
    var athleteA: Athlete,

    @ManyToOne
    @JoinColumn(name = "athlete_b_id", nullable = false)
    var athleteB: Athlete,

    @ManyToOne
    @JoinColumn(name = "winner_id")
    var winner: Athlete? = null,

    @Column(nullable = false)
    var stage: String,

    @Column(name = "table_number")
    var tableNumber: Int? = null,

    @Column(name = "match_number")
    var matchNumber: Int? = null,

    @Column(name = "start_time")
    var startTime: LocalDateTime? = null,

    @Column(name = "end_time")
    var endTime: LocalDateTime? = null,

    @OneToOne(mappedBy = "match", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var protocol: MatchProtocol? = null
)