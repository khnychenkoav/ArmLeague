package com.armleague.backend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.Objects

@Entity
@Table(name = "weight_classes")
class WeightClass(
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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WeightClass
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}