package com.slashgil.formula1.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.usecase.GetDriverStandingsUseCase
import com.slashgil.formula1.domain.usecase.GetDriversUseCase
import com.slashgil.formula1.domain.usecase.GetRaceScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class F1Tab { STANDINGS, DRIVERS, SCHEDULE }

sealed interface F1UiState {
    data object Loading : F1UiState
    data class Success(
        val season: String = "2024",
        val selectedTab: F1Tab = F1Tab.STANDINGS,
        val standings: List<DriverStanding> = emptyList(),
        val drivers: List<Driver> = emptyList(),
        val schedule: List<Race> = emptyList()
    ) : F1UiState
    data class Error(val message: String) : F1UiState
}

class F1ViewModel(
    private val getDriversUseCase: GetDriversUseCase,
    private val getDriverStandingsUseCase: GetDriverStandingsUseCase,
    private val getRaceScheduleUseCase: GetRaceScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<F1UiState>(F1UiState.Loading)
    val uiState: StateFlow<F1UiState> = _uiState.asStateFlow()

    init {
        loadData("2024")
    }

    fun loadData(season: String = "2024") {
        viewModelScope.launch {
            _uiState.value = F1UiState.Loading

            val standingsResult = getDriverStandingsUseCase(season)
            val driversResult = getDriversUseCase(season)
            val scheduleResult = getRaceScheduleUseCase(season)

            val standings = standingsResult.getOrDefault(emptyList())
            val drivers = driversResult.getOrDefault(emptyList())
            val schedule = scheduleResult.getOrDefault(emptyList())

            if (standings.isNotEmpty() || drivers.isNotEmpty() || schedule.isNotEmpty()) {
                _uiState.value = F1UiState.Success(
                    season = season,
                    selectedTab = F1Tab.STANDINGS,
                    standings = standings,
                    drivers = drivers,
                    schedule = schedule
                )
            } else {
                val errorMsg = standingsResult.exceptionOrNull()?.message
                    ?: driversResult.exceptionOrNull()?.message
                    ?: "Failed to load F1 Telemetry data."
                _uiState.value = F1UiState.Error(errorMsg)
            }
        }
    }

    fun selectTab(tab: F1Tab) {
        val currentState = _uiState.value
        if (currentState is F1UiState.Success) {
            _uiState.value = currentState.copy(selectedTab = tab)
        }
    }
}