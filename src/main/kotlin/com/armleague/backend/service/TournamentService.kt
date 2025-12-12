package com.armleague.backend.service

import com.armleague.backend.dto.UpdateTournamentDto
import com.armleague.backend.model.Tournament
import com.armleague.backend.repository.TournamentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TournamentService(
    private val repository: TournamentRepository
) {


    fun getAllTournaments(): List<Tournament> = repository.findAll()


    fun createTournament(tournament: Tournament): Tournament {
        //if (tournament.startDate.isBefore(LocalDateTime.now())) {
        //    throw IllegalArgumentException("Tournament cannot start in the past")
        //}
        return repository.save(tournament)
    }

    fun getTournamentById(id: Int): Tournament {
        return repository.findById(id).orElseThrow {
            RuntimeException("Tournament not found with id: $id")
        }
    }

    fun deleteTournament(id: Int) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw RuntimeException("Tournament not found")
        }
    }

    fun updateTournament(id: Int, dto: UpdateTournamentDto): Tournament {
        val tournament = getTournamentById(id)

        dto.name?.let { tournament.name = it }
        dto.description?.let { tournament.description = it }
        dto.country?.let { tournament.country = it }
        dto.location?.let { tournament.location = it }
        dto.startDate?.let { tournament.startDate = it }
        dto.endDate?.let { tournament.endDate = it }
        dto.prizePool?.let { tournament.prizePool = it }
        dto.status?.let { tournament.status = it }

        return repository.save(tournament)
    }
}