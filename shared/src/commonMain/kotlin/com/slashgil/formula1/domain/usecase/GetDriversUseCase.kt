package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.repository.F1Repository

class GetDriversUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(season: String? = null): Result<List<Driver>> {
        return repository.getDrivers(season)
    }
}