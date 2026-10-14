package com.slashgil.formula1.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slashgil.formula1.data.remote.dto.WikipediaSummaryDto
import com.slashgil.formula1.domain.model.Constructor
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.model.RaceResult
import com.slashgil.formula1.domain.usecase.GetConstructorStandingsUseCase
import com.slashgil.formula1.domain.usecase.GetDriverStandingsUseCase
import com.slashgil.formula1.domain.usecase.GetDriversUseCase
import com.slashgil.formula1.domain.usecase.GetRaceResultsUseCase
import com.slashgil.formula1.domain.usecase.GetRaceScheduleUseCase
import com.slashgil.formula1.domain.usecase.GetWikipediaSummaryUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class F1Tab { STANDINGS, DRIVERS, SCHEDULE }

sealed interface DetailItem {
    data class DriverDetail(
        val driver: Driver,
        val standing: DriverStanding? = null,
        val wikiSummary: WikipediaSummaryDto? = null,
        val isLoadingWiki: Boolean = false
    ) : DetailItem

    data class ConstructorDetail(
        val constructor: Constructor,
        val wikiSummary: WikipediaSummaryDto? = null,
        val isLoadingWiki: Boolean = false
    ) : DetailItem

    data class RaceDetail(
        val race: Race,
        val results: List<RaceResult> = emptyList(),
        val wikiSummary: WikipediaSummaryDto? = null,
        val isLoadingResults: Boolean = false,
        val isLoadingWiki: Boolean = false
    ) : DetailItem
}

sealed interface F1UiState {
    data object Loading : F1UiState
    data class Success(
        val season: String = "2024",
        val availableSeasons: List<String> = listOf("2026", "2025", "2024", "2023", "2022", "2021", "2020"),
        val selectedTab: F1Tab = F1Tab.STANDINGS,
        val standings: List<DriverStanding> = emptyList(),
        val drivers: List<Driver> = emptyList(),
        val schedule: List<Race> = emptyList(),
        val isStandingsLoading: Boolean = false,
        val isDriversLoading: Boolean = false,
        val isScheduleLoading: Boolean = false,
        val selectedDetail: DetailItem? = null
    ) : F1UiState
    data class Error(val message: String) : F1UiState
}

class F1ViewModel(
    private val getDriversUseCase: GetDriversUseCase,
    private val getDriverStandingsUseCase: GetDriverStandingsUseCase,
    private val getRaceScheduleUseCase: GetRaceScheduleUseCase,
    private val getRaceResultsUseCase: GetRaceResultsUseCase,
    private val getWikipediaSummaryUseCase: GetWikipediaSummaryUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<F1UiState>(F1UiState.Loading)
    val uiState: StateFlow<F1UiState> = _uiState.asStateFlow()

    init {
        loadData("2024")
    }

    fun loadData(season: String = "2024") {
        viewModelScope.launch(ioDispatcher) {
            val currentTab = (_uiState.value as? F1UiState.Success)?.selectedTab ?: F1Tab.STANDINGS
            _uiState.value = F1UiState.Success(
                season = season,
                selectedTab = currentTab,
                isStandingsLoading = true,
                isDriversLoading = true,
                isScheduleLoading = true
            )

            // Concurrently fetch Standings on ioDispatcher
            launch(ioDispatcher) {
                val standings = getDriverStandingsUseCase(season).getOrDefault(emptyList())
                updateSuccessState { currentState ->
                    currentState.copy(
                        standings = standings,
                        isStandingsLoading = false
                    )
                }
            }

            // Concurrently fetch Drivers on ioDispatcher
            launch(ioDispatcher) {
                val drivers = getDriversUseCase(season).getOrDefault(emptyList())
                updateSuccessState { currentState ->
                    currentState.copy(
                        drivers = drivers,
                        isDriversLoading = false
                    )
                }
            }

            // Concurrently fetch Race Schedule on ioDispatcher
            launch(ioDispatcher) {
                val schedule = getRaceScheduleUseCase(season).getOrDefault(emptyList())
                updateSuccessState { currentState ->
                    currentState.copy(
                        schedule = schedule,
                        isScheduleLoading = false
                    )
                }
            }
        }
    }

    private fun updateSuccessState(transform: (F1UiState.Success) -> F1UiState.Success) {
        val currentState = _uiState.value as? F1UiState.Success
        if (currentState != null) {
            _uiState.value = transform(currentState)
        }
    }

    fun selectSeason(season: String) {
        val currentState = _uiState.value
        if (currentState is F1UiState.Success && currentState.season != season) {
            loadData(season)
        }
    }

    fun selectTab(tab: F1Tab) {
        val currentState = _uiState.value
        if (currentState is F1UiState.Success) {
            _uiState.value = currentState.copy(selectedTab = tab)
        }
    }

    fun openDriverDetail(driver: Driver, standing: DriverStanding? = null) {
        val currentState = _uiState.value as? F1UiState.Success ?: return
        val detail = DetailItem.DriverDetail(driver = driver, standing = standing, isLoadingWiki = true)
        _uiState.value = currentState.copy(selectedDetail = detail)

        viewModelScope.launch(ioDispatcher) {
            val wikiSummary = getWikipediaSummaryUseCase(driver.url).getOrNull()
            val latestState = _uiState.value as? F1UiState.Success
            if (latestState?.selectedDetail is DetailItem.DriverDetail) {
                val currentDetail = latestState.selectedDetail as DetailItem.DriverDetail
                if (currentDetail.driver.driverId == driver.driverId) {
                    _uiState.value = latestState.copy(
                        selectedDetail = currentDetail.copy(
                            wikiSummary = wikiSummary,
                            isLoadingWiki = false
                        )
                    )
                }
            }
        }
    }

    fun openConstructorDetail(constructor: Constructor) {
        val currentState = _uiState.value as? F1UiState.Success ?: return
        val detail = DetailItem.ConstructorDetail(constructor = constructor, isLoadingWiki = true)
        _uiState.value = currentState.copy(selectedDetail = detail)

        viewModelScope.launch(ioDispatcher) {
            val wikiSummary = getWikipediaSummaryUseCase(constructor.url).getOrNull()
            val latestState = _uiState.value as? F1UiState.Success
            if (latestState?.selectedDetail is DetailItem.ConstructorDetail) {
                val currentDetail = latestState.selectedDetail as DetailItem.ConstructorDetail
                if (currentDetail.constructor.constructorId == constructor.constructorId) {
                    _uiState.value = latestState.copy(
                        selectedDetail = currentDetail.copy(
                            wikiSummary = wikiSummary,
                            isLoadingWiki = false
                        )
                    )
                }
            }
        }
    }

    fun openRaceDetail(race: Race) {
        val currentState = _uiState.value as? F1UiState.Success ?: return
        val detail = DetailItem.RaceDetail(race = race, isLoadingResults = true, isLoadingWiki = true)
        _uiState.value = currentState.copy(selectedDetail = detail)

        viewModelScope.launch(ioDispatcher) {
            val results = getRaceResultsUseCase(race.season, race.round).getOrDefault(emptyList())
            val wikiSummary = getWikipediaSummaryUseCase(race.circuit.url.ifBlank { race.url }).getOrNull()

            val latestState = _uiState.value as? F1UiState.Success
            if (latestState?.selectedDetail is DetailItem.RaceDetail) {
                val currentDetail = latestState.selectedDetail as DetailItem.RaceDetail
                if (currentDetail.race.round == race.round && currentDetail.race.season == race.season) {
                    _uiState.value = latestState.copy(
                        selectedDetail = currentDetail.copy(
                            results = results,
                            wikiSummary = wikiSummary,
                            isLoadingResults = false,
                            isLoadingWiki = false
                        )
                    )
                }
            }
        }
    }

    fun closeDetail() {
        val currentState = _uiState.value as? F1UiState.Success ?: return
        _uiState.value = currentState.copy(selectedDetail = null)
    }
}