package com.armleague.backend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "weight_classes")
data class WeightClass(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    @JsonIgnore
    val tournament: Tournament,

    @Column(name = "class_name", nullable = false)
    var className: String,

    @Column(name = "max_weight_kg", nullable = false)
    var maxWeightKg: BigDecimal,

    @Enumerated(EnumType.STRING)
    var gender: Gender,

    @Enumerated(EnumType.STRING)
    var hand: HandType,

    @Column(name = "entry_fee")
    var entryFee: BigDecimal = BigDecimal.ZERO
)