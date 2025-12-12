package com.armleague.backend.controller

import com.armleague.backend.dto.UpdateTournamentDto
import com.armleague.backend.model.Tournament
import com.armleague.backend.service.TournamentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Tournaments", description = "Управление турнирами")
class TournamentController(
    private val service: TournamentService
) {

    @GetMapping
    @Operation(summary = "Получить список всех турниров")
    fun getAll(): List<Tournament> {
        return service.getAllTournaments()
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить турнир по ID")
    fun getOne(@PathVariable id: Int): Tournament {
        return service.getTournamentById(id)
    }

    @PostMapping
    @Operation(summary = "Создать новый турнир")
    fun create(@RequestBody tournament: Tournament): Tournament {
        return service.createTournament(tournament)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные турнира (статус, даты, описание)")
    fun update(
        @PathVariable id: Int,
        @RequestBody dto: UpdateTournamentDto
    ): Tournament {
        return service.updateTournament(id, dto)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить турнир")
    fun delete(@PathVariable id: Int) {
        service.deleteTournament(id)
    }
}