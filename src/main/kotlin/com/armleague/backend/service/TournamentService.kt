package com.armleague.backend.service

import com.armleague.backend.dto.TournamentGridDto
import com.armleague.backend.dto.UpdateTournamentDto
import com.armleague.backend.model.Tournament
import com.armleague.backend.repository.MatchRepository
import com.armleague.backend.repository.TournamentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TournamentService(
    private val repository: TournamentRepository,
    private val matchRepository: MatchRepository,
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

    fun getTournamentGrid(id: Int): TournamentGridDto {
        val tournament = getTournamentById(id)
        val matches = matchRepository.findByTournamentId(id)

        val grid = matches.groupBy { it.weightClass.className }

        return TournamentGridDto(tournament = tournament, grid = grid)
    }

    fun batchCreateTournaments(tournaments: List<Tournament>, dryRun: Boolean): Map<String, Any> {
        var successCount = 0
        val errors = mutableListOf<String>()

        tournaments.forEachIndexed { index, tournament ->
            try {
                if (tournament.name.isBlank()) {
                    throw IllegalArgumentException("Tournament name cannot be blank")
                }
                if (tournament.endDate.isBefore(tournament.startDate)) {
                    throw IllegalArgumentException("End date must be after start date")
                }

                if (!dryRun) {
                    repository.save(tournament)
                }
                successCount++

            } catch (e: Exception) {
                errors.add("Error at index $index ('${tournament.name}'): ${e.message}")
            }
        }

        return mapOf(
            "total" to tournaments.size,
            "processed" to successCount,
            "errors" to errors,
            "dryRun" to dryRun
        )
    }
}