package com.armleague.backend.controller

import com.armleague.backend.dto.CreateRefereeDto
import com.armleague.backend.dto.UpdateRefereeDto
import com.armleague.backend.model.Referee
import com.armleague.backend.service.RefereeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/referees")
@Tag(name = "Referees", description = "Управление судьями")
class RefereeController(
    private val service: RefereeService
) {

    @GetMapping
    @Operation(summary = "Получить список всех судей")
    fun getAll(): List<Referee> = service.getAllReferees()

    @GetMapping("/{id}")
    @Operation(summary = "Получить судью по ID")
    fun getById(@PathVariable id: Int): Referee = service.getRefereeById(id)

    @PostMapping
    @Operation(summary = "Создать нового судью (и его User аккаунт)")
    fun create(@RequestBody dto: CreateRefereeDto): Referee = service.createReferee(dto)

    @PutMapping("/{id}")
    @Operation(summary = "Обновить профиль судьи")
    fun update(@PathVariable id: Int, @RequestBody dto: UpdateRefereeDto): Referee {
        return service.updateReferee(id, dto)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить судью (и его User аккаунт)")
    fun delete(@PathVariable id: Int) {
        service.deleteReferee(id)
    }
}