package com.armleague.backend.controller

import com.armleague.backend.dto.TopAthleteReportDto
import com.armleague.backend.model.AthleteSummaryView
import com.armleague.backend.model.MatchScheduleView
import com.armleague.backend.model.RefereeWorkloadView
import com.armleague.backend.repository.AthleteFunctionsRepository
import com.armleague.backend.repository.AthleteRepository
import com.armleague.backend.repository.AthleteSummaryRepository
import com.armleague.backend.repository.MatchScheduleRepository
import com.armleague.backend.repository.RefereeWorkloadRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Отчеты и статистика")
class AnalyticsController(
    private val athleteRepository: AthleteRepository,
    private val athleteSummaryRepo: AthleteSummaryRepository,
    private val matchScheduleRepo: MatchScheduleRepository,
    private val refereeWorkloadRepo: RefereeWorkloadRepository,
    private val athleteFunctionsRepo: AthleteFunctionsRepository
) {

    @GetMapping("/top-winners")
    @Operation(summary = "Топ-10 атлетов по количеству побед")
    fun getTopWinners(): List<TopAthleteReportDto> {
        val rawResults = athleteRepository.findTopAthletesByWins()

        return rawResults.map { row ->
            TopAthleteReportDto(
                nickname = (row[0] as? String) ?: "N/A",
                country = (row[1] as? String) ?: "N/A",
                wins = (row[2] as Number).toInt()
            )
        }
    }

    @GetMapping("/athletes-summary")
    @Operation(summary = "Сводка по атлетам")
    fun getAthletesSummary(): List<AthleteSummaryView> {
        return athleteSummaryRepo.findAll()
    }

    @GetMapping("/match-schedule")
    @Operation(summary = "Расписание матчей")
    fun getMatchSchedule(): List<MatchScheduleView> {
        return matchScheduleRepo.findAll()
    }

    @GetMapping("/referee-workload")
    @Operation(summary = "Нагрузка на судей")
    fun getRefereeWorkload(): List<RefereeWorkloadView> {
        return refereeWorkloadRepo.findAll()
    }

    @GetMapping("/bmi-calculator")
    @Operation(summary = "Расчет ИМТ через SQL-функцию")
    fun calculateBmi(
        @RequestParam weight: BigDecimal,
        @RequestParam height: Int
    ): Map<String, BigDecimal> {
        val bmi = athleteFunctionsRepo.calculateBmi(weight, height)
        return mapOf("bmi" to bmi)
    }

    @GetMapping("/athlete-history/{athleteId}")
    @Operation(summary = "История матчей атлета через SQL-функцию")
    fun getHistory(@PathVariable athleteId: Int): List<Map<String, Any>> {
        val history = athleteFunctionsRepo.getAthleteHistory(athleteId)

        return history.map {
            mapOf(
                "date" to it.getMatchDate(),
                "opponent" to it.getOpponentName(),
                "result" to it.getResult(),
                "stage" to it.getStage()
            )
        }
    }
}