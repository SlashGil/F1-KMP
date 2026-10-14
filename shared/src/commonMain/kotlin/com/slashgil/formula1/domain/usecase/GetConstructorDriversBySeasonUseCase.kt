package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.repository.F1Repository

class GetConstructorDriversBySeasonUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String, constructorId: String): Result<List<Driver>> {
        return repository.getConstructorDriversBySeason(season, constructorId)
    }
}