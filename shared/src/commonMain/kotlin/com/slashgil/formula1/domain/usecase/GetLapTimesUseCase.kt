package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.LapTiming
import com.slashgil.formula1.domain.repository.F1Repository

class GetLapTimesUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String, round: Int): Result<List<LapTiming>> {
        return repository.getLapTimes(season, round)
    }
}