package com.armleague.backend.controller

import com.armleague.backend.dto.RegisterAthleteDto
import com.armleague.backend.service.AthleteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/batch")
@Tag(name = "Batch Import", description = "Массовая загрузка данных")
class BatchController(
    private val athleteService: AthleteService
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
}