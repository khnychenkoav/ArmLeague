package com.armleague.backend.repository

import com.armleague.backend.model.Athlete
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AthleteRepository : JpaRepository<Athlete, Int> {
    fun findByUserId(userId: Int): Athlete?

    @Query(
        value = """
            SELECT 
                a.nickname, 
                a.country_code, 
                r.wins
            FROM rankings r
            JOIN athletes a ON r.athlete_id = a.athlete_id
            WHERE r.wins > 0
            ORDER BY r.wins DESC, r.points DESC
            LIMIT 10
        """,
        nativeQuery = true
    )
    fun findTopAthletesByWins(): List<Array<Any>>
}