package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.ConstructorStanding
import com.slashgil.formula1.domain.repository.F1Repository

class GetConstructorStandingsUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String): Result<List<ConstructorStanding>> {
        return repository.getConstructorStandings(season)
    }
}