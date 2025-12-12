package com.armleague.backend.controller

import com.armleague.backend.model.Ranking
import com.armleague.backend.service.RankingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rankings")
@Tag(name = "Rankings", description = "Просмотр рейтингов")
class RankingController(
    private val service: RankingService
) {

    @GetMapping
    @Operation(summary = "Получить общий рейтинг всех атлетов")
    fun getAll(): List<Ranking> {
        return service.getAllRankings()
    }

    @GetMapping("/athlete/{athleteId}")
    @Operation(summary = "Получить рейтинг конкретного атлета (на обе руки)")
    fun getByAthlete(@PathVariable athleteId: Int): List<Ranking> {
        return service.getRankingsByAthlete(athleteId)
    }

    @GetMapping("/raw-check")
    @Operation(summary = "Получить первые 20 записей из таблицы рейтингов")
    fun rawCheck(): List<Ranking> {
        return service.getAllRankings().take(20)
    }
}