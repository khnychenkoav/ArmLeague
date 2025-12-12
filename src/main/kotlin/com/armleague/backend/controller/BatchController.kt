package com.armleague.backend.controller

import com.armleague.backend.dto.CreateWeightClassDto
import com.armleague.backend.dto.RegisterAthleteDto
import com.armleague.backend.model.Tournament
import com.armleague.backend.service.AthleteService
import com.armleague.backend.service.RegistrationService
import com.armleague.backend.service.TournamentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/batch")
@Tag(name = "Batch Import", description = "Массовая загрузка данных")
class BatchController(
    private val athleteService: AthleteService,
    private val tournamentService: TournamentService,
    private val registrationService: RegistrationService,
) {

    @PostMapping("/athletes")
    @Operation(summary = "Массовый импорт атлетов (списком)")
    fun importAthletes(@RequestBody dtos: List<RegisterAthleteDto>): Map<String, Any> {
        var successCount = 0
        val errors = mutableListOf<String>()

        dtos.forEach { dto ->
            try {
                athleteService.registerNewAthlete(dto)
                successCount++
            } catch (e: Exception) {
                errors.add("Error importing ${dto.email}: ${e.message}")
            }
        }

        return mapOf(
            "total" to dtos.size,
            "imported" to successCount,
            "errors" to errors
        )
    }

    @PostMapping("/tournaments")
    @Operation(summary = "Массовый импорт турниров (с параметром dryRun)")
    fun importTournaments(
        @RequestBody tournaments: List<Tournament>,
        @RequestParam(defaultValue = "false") dryRun: Boolean
    ): Map<String, Any> {
        return tournamentService.batchCreateTournaments(tournaments, dryRun)
    }

    @PostMapping("/tournaments/{tournamentId}/classes")
    @Operation(summary = "Массовое создание весовых категорий для турнира")
    fun importClasses(
        @PathVariable tournamentId: Int,
        @RequestBody dtos: List<CreateWeightClassDto>,
        @RequestParam(defaultValue = "false") dryRun: Boolean
    ): Map<String, Any> {
        return registrationService.batchCreateWeightClasses(tournamentId, dtos, dryRun)
    }
}