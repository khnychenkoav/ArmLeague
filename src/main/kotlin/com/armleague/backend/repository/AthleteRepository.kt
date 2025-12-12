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
            SELECT a.nickname, a.country_code, COUNT(m.match_id) as wins
            FROM athletes a
            JOIN matches m ON m.winner_id = a.athlete_id
            GROUP BY a.athlete_id, a.nickname, a.country_code
            ORDER BY wins DESC
            LIMIT 10
        """,
        nativeQuery = true
    )
    fun findTopAthletesByWins(): List<Array<Any>>
}