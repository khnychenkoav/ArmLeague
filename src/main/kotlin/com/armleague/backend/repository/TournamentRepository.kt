package com.armleague.backend.repository

import com.armleague.backend.model.Tournament
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TournamentRepository : JpaRepository<Tournament, Int> {

}