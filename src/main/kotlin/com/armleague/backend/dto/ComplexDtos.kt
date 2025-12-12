package com.armleague.backend.dto

import com.armleague.backend.model.Athlete
import com.armleague.backend.model.Match
import com.armleague.backend.model.Ranking
import com.armleague.backend.model.Tournament

data class TournamentGridDto(
    val tournament: Tournament,
    val grid: Map<String, List<Match>>
)

data class AthleteProfileDto(
    val athlete: Athlete,
    val rankings: List<Ranking>,
    val matchHistory: List<Match>
)