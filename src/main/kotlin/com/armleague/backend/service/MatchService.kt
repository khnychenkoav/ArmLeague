package com.armleague.backend.service

import com.armleague.backend.dto.CreateMatchDto
import com.armleague.backend.dto.SetMatchResultDto
import com.armleague.backend.dto.UpdateMatchDto
import com.armleague.backend.model.Match
import com.armleague.backend.model.MatchProtocol
import com.armleague.backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val tournamentRepository: TournamentRepository,
    private val weightClassRepository: WeightClassRepository,
    private val athleteRepository: AthleteRepository,
    private val protocolRepository: MatchProtocolRepository,
    private val auditService: AuditService,
) {

    fun getMatchesByTournament(tournamentId: Int): List<Match> {
        return matchRepository.findByTournamentId(tournamentId)
    }

    fun getMatchById(id: Int): Match {
        return matchRepository.findById(id).orElseThrow {
            RuntimeException("Match not found with id: $id")
        }
    }

    @Transactional
    fun createMatch(dto: CreateMatchDto): Match {
        val tournament = tournamentRepository.findById(dto.tournamentId)
            .orElseThrow { RuntimeException("Tournament not found") }
        val weightClass = weightClassRepository.findById(dto.weightClassId)
            .orElseThrow { RuntimeException("Weight Class not found") }
        val athleteA = athleteRepository.findById(dto.athleteAId)
            .orElseThrow { RuntimeException("Athlete A not found") }
        val athleteB = athleteRepository.findById(dto.athleteBId)
            .orElseThrow { RuntimeException("Athlete B not found") }

        val match = Match(
            tournament = tournament,
            weightClass = weightClass,
            athleteA = athleteA,
            athleteB = athleteB,
            stage = dto.stage,
            tableNumber = dto.tableNumber,
            matchNumber = dto.matchNumber,
            startTime = dto.startTime
        )
        return matchRepository.save(match)
    }

    @Transactional
    fun updateMatch(id: Int, dto: UpdateMatchDto): Match {
        val match = getMatchById(id)

        dto.stage?.let { match.stage = it }
        dto.tableNumber?.let { match.tableNumber = it }
        dto.startTime?.let { match.startTime = it }
        dto.endTime?.let { match.endTime = it }

        dto.athleteAId?.let { match.athleteA = athleteRepository.findById(it).get() }
        dto.athleteBId?.let { match.athleteB = athleteRepository.findById(it).get() }

        dto.winnerId?.let {
            match.winner = athleteRepository.findById(it).orElseThrow { RuntimeException("Winner not found") }
        }

        return matchRepository.save(match)
    }

    fun deleteMatch(id: Int) {
        if (matchRepository.existsById(id)) {
            matchRepository.deleteById(id)
        } else {
            throw RuntimeException("Match not found")
        }
    }


    @Transactional
    fun setMatchResult(matchId: Int, dto: SetMatchResultDto): Match {
        val match = getMatchById(matchId)
        val winner = athleteRepository.findById(dto.winnerId)
            .orElseThrow { RuntimeException("Winner not found") }

        if (match.athleteA.id != winner.id && match.athleteB.id != winner.id) {
            throw RuntimeException("Winner must be one of the participants!")
        }

        match.winner = winner
        match.endTime = LocalDateTime.now()

        val protocol = match.protocol ?: MatchProtocol(
            match = match,
            winningHand = dto.winningHand,
            resultType = dto.resultType
        )

        protocol.winningHand = dto.winningHand
        protocol.resultType = dto.resultType
        protocol.durationSeconds = dto.durationSeconds
        protocol.foulsA = dto.foulsA
        protocol.foulsB = dto.foulsB
        protocol.isStrapMatch = dto.isStrapMatch
        protocol.notes = dto.notes

        protocolRepository.save(protocol)

        match.protocol = protocol

        val savedMatch = matchRepository.save(match)

        return savedMatch
    }
}