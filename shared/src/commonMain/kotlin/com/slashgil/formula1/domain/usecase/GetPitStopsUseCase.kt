package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.PitStop
import com.slashgil.formula1.domain.repository.F1Repository

class GetPitStopsUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String, round: Int): Result<List<PitStop>> {
        return repository.getPitStops(season, round)
    }
}