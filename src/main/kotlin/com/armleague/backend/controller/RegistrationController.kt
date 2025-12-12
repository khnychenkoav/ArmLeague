package com.armleague.backend.controller

import com.armleague.backend.dto.CreateRegistrationDto
import com.armleague.backend.dto.CreateWeightClassDto
import com.armleague.backend.dto.UpdateRegistrationDto
import com.armleague.backend.dto.UpdateWeightClassDto
import com.armleague.backend.dto.WeighInDto
import com.armleague.backend.model.TournamentRegistration
import com.armleague.backend.model.WeightClass
import com.armleague.backend.service.RegistrationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@Tag(name = "Registrations", description = "Весовые категории и Заявки")
class RegistrationController(
    private val service: RegistrationService
) {

    @PostMapping("/tournaments/{tournamentId}/classes")
    @Operation(summary = "Добавить весовую категорию в турнир")
    fun addClass(
        @PathVariable tournamentId: Int,
        @RequestBody dto: CreateWeightClassDto
    ): WeightClass {
        return service.addWeightClass(tournamentId, dto)
    }

    @GetMapping("/tournaments/{tournamentId}/classes")
    @Operation(summary = "Получить список категорий турнира")
    fun getClasses(@PathVariable tournamentId: Int): List<WeightClass> {
        return service.getClassesByTournament(tournamentId)
    }


    @PostMapping("/registrations")
    @Operation(summary = "Подать заявку на участие")
    fun register(@RequestBody dto: CreateRegistrationDto): TournamentRegistration {
        return service.registerAthlete(dto)
    }

    @GetMapping("/tournaments/{tournamentId}/registrations")
    @Operation(summary = "Посмотреть кто заявился на турнир")
    fun getRegistrations(@PathVariable tournamentId: Int): List<TournamentRegistration> {
        return service.getRegistrations(tournamentId)
    }

    @GetMapping("/registrations/{id}")
    @Operation(summary = "Получить детали заявки по ID")
    fun getOne(@PathVariable id: Int): TournamentRegistration {
        return service.getRegistrationById(id)
    }

    @PutMapping("/registrations/{id}")
    @Operation(summary = "Обновить заявку (взвешивание, статус, оплата, смена категории)")
    fun update(
        @PathVariable id: Int,
        @RequestBody dto: UpdateRegistrationDto
    ): TournamentRegistration {
        return service.updateRegistration(id, dto)
    }

    @DeleteMapping("/registrations/{id}")
    @Operation(summary = "Удалить заявку (снятие с турнира)")
    fun delete(@PathVariable id: Int) {
        service.deleteRegistration(id)
    }

    @GetMapping("/weight-classes/{id}")
    @Operation(summary = "Получить информацию о категории")
    fun getClass(@PathVariable id: Int): WeightClass {
        return service.getWeightClassById(id)
    }

    @PutMapping("/weight-classes/{id}")
    @Operation(summary = "Изменить категорию (вес, руку, взнос)")
    fun updateClass(
        @PathVariable id: Int,
        @RequestBody dto: UpdateWeightClassDto
    ): WeightClass {
        return service.updateWeightClass(id, dto)
    }

    @DeleteMapping("/weight-classes/{id}")
    @Operation(summary = "Удалить категорию (Внимание: удалит и все заявки в ней!)")
    fun deleteClass(@PathVariable id: Int) {
        service.deleteWeightClass(id)
    }

    @PostMapping("/registrations/{id}/weigh-in")
    @Operation(summary = "Провести взвешивание (Транзакция: обновляет и заявку, и профиль атлета)")
    fun weighIn(
        @PathVariable id: Int,
        @RequestBody dto: WeighInDto
    ): TournamentRegistration {
        return service.performWeighIn(id, dto)
    }
}