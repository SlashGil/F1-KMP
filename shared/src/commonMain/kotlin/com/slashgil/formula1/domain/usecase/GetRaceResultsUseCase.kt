package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.RaceResult
import com.slashgil.formula1.domain.repository.F1Repository

class GetRaceResultsUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String, round: Int): Result<List<RaceResult>> {
        return repository.getRaceResults(season, round)
    }
}