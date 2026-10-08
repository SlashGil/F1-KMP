package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.repository.F1Repository

class GetRaceScheduleUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String): Result<List<Race>> {
        return repository.getSchedule(season)
    }
}