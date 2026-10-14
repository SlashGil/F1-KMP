package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.data.remote.dto.WikipediaSummaryDto
import com.slashgil.formula1.domain.repository.F1Repository

class GetWikipediaSummaryUseCase(private val repository: F1Repository) {
    suspend operator fun invoke(wikiUrl: String): Result<WikipediaSummaryDto> {
        return repository.getWikipediaSummary(wikiUrl)
    }
}