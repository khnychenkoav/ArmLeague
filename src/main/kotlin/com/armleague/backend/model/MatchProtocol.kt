package com.armleague.backend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "match_protocols")
data class MatchProtocol(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "protocol_id")
    val id: Int? = null,

    @OneToOne
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    @JsonIgnore // Чтобы не зациклить JSON
    val match: Match,

    @Enumerated(EnumType.STRING)
    @Column(name = "winning_hand", nullable = false)
    var winningHand: HandType,

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", nullable = false)
    var resultType: MatchResultType,

    @Column(name = "duration_seconds")
    var durationSeconds: Int? = null,

    @Column(name = "fouls_a")
    var foulsA: Int = 0,

    @Column(name = "fouls_b")
    var foulsB: Int = 0,

    @Column(name = "is_strap_match")
    var isStrapMatch: Boolean = false,

    @Column(name = "notes")
    var notes: String? = null
)