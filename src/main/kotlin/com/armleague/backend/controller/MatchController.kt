package com.armleague.backend.controller

import com.armleague.backend.dto.CreateMatchDto
import com.armleague.backend.dto.SetMatchResultDto
import com.armleague.backend.dto.UpdateMatchDto
import com.armleague.backend.model.Match
import com.armleague.backend.service.MatchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Matches", description = "Управление поединками")
class MatchController(
    private val service: MatchService
) {

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Получить все матчи турнира")
    fun getAllByTournament(@PathVariable tournamentId: Int): List<Match> {
        return service.getMatchesByTournament(tournamentId)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить матч по ID")
    fun getOne(@PathVariable id: Int): Match {
        return service.getMatchById(id)
    }

    @PostMapping
    @Operation(summary = "Создать матч (пару)")
    fun create(@RequestBody dto: CreateMatchDto): Match {
        return service.createMatch(dto)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить информацию о матче")
    fun update(
        @PathVariable id: Int,
        @RequestBody dto: UpdateMatchDto
    ): Match {
        return service.updateMatch(id, dto)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить матч")
    fun delete(@PathVariable id: Int) {
        service.deleteMatch(id)
    }

    @PostMapping("/{id}/result")
    @Operation(summary = "Зафиксировать результат (создает протокол + ставит победителя)")
    fun setResult(
        @PathVariable id: Int,
        @RequestBody dto: SetMatchResultDto
    ): Match {
        return service.setMatchResult(id, dto)
    }
}