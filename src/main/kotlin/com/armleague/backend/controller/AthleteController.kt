package com.armleague.backend.controller

import com.armleague.backend.dto.RegisterAthleteDto
import com.armleague.backend.dto.UpdateAthleteDto
import com.armleague.backend.model.Athlete
import com.armleague.backend.service.AthleteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/athletes")
@Tag(name = "Athletes", description = "Управление атлетами")
class AthleteController(
    private val service: AthleteService
) {

    @GetMapping
    @Operation(summary = "Получить всех атлетов")
    fun getAll(): List<Athlete> {
        return service.getAllAthletes()
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового атлета")
    fun register(@RequestBody dto: RegisterAthleteDto): Athlete {
        return service.registerNewAthlete(dto)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить профиль атлета по ID")
    fun getOne(@PathVariable id: Int): Athlete {
        return service.getAthleteById(id)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить профиль атлета (передавать только то, что нужно изменить)")
    fun update(
        @PathVariable id: Int,
        @RequestBody dto: UpdateAthleteDto
    ): Athlete {
        return service.updateAthlete(id, dto)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить атлета (удаляет и связанный User аккаунт)")
    fun delete(@PathVariable id: Int) {
        service.deleteAthlete(id)
    }
}