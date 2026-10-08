package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.repository.F1Repository

class GetDriverStandingsUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String): Result<List<DriverStanding>> {
        return repository.getDriverStandings(season)
    }
}