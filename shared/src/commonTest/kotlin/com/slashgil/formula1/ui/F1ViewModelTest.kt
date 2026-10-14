package com.slashgil.formula1.ui

import com.slashgil.formula1.domain.usecase.FakeF1Repository
import com.slashgil.formula1.domain.usecase.GetConstructorDriversBySeasonUseCase
import com.slashgil.formula1.domain.usecase.GetConstructorStandingsUseCase
import com.slashgil.formula1.domain.usecase.GetDriverResultsBySeasonUseCase
import com.slashgil.formula1.domain.usecase.GetDriverStandingsUseCase
import com.slashgil.formula1.domain.usecase.GetDriversUseCase
import com.slashgil.formula1.domain.usecase.GetPitStopsUseCase
import com.slashgil.formula1.domain.usecase.GetRaceResultsUseCase
import com.slashgil.formula1.domain.usecase.GetRaceScheduleUseCase
import com.slashgil.formula1.domain.usecase.GetWikipediaSummaryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class F1ViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepository = FakeF1Repository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testViewModelInitialDataLoading() = runTest {
        val viewModel = F1ViewModel(
            getDriversUseCase = GetDriversUseCase(fakeRepository),
            getDriverStandingsUseCase = GetDriverStandingsUseCase(fakeRepository),
            getConstructorStandingsUseCase = GetConstructorStandingsUseCase(fakeRepository),
            getRaceScheduleUseCase = GetRaceScheduleUseCase(fakeRepository),
            getRaceResultsUseCase = GetRaceResultsUseCase(fakeRepository),
            getWikipediaSummaryUseCase = GetWikipediaSummaryUseCase(fakeRepository),
            getPitStopsUseCase = GetPitStopsUseCase(fakeRepository),
            getDriverResultsBySeasonUseCase = GetDriverResultsBySeasonUseCase(fakeRepository),
            getConstructorDriversBySeasonUseCase = GetConstructorDriversBySeasonUseCase(fakeRepository),
            ioDispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is F1UiState.Success)
        assertEquals("2024", state.season)
        assertEquals(1, state.standings.size)
        assertEquals(1, state.drivers.size)
        assertEquals(1, state.schedule.size)
    }

    @Test
    fun testViewModelTabSelection() = runTest {
        val viewModel = F1ViewModel(
            getDriversUseCase = GetDriversUseCase(fakeRepository),
            getDriverStandingsUseCase = GetDriverStandingsUseCase(fakeRepository),
            getConstructorStandingsUseCase = GetConstructorStandingsUseCase(fakeRepository),
            getRaceScheduleUseCase = GetRaceScheduleUseCase(fakeRepository),
            getRaceResultsUseCase = GetRaceResultsUseCase(fakeRepository),
            getWikipediaSummaryUseCase = GetWikipediaSummaryUseCase(fakeRepository),
            getPitStopsUseCase = GetPitStopsUseCase(fakeRepository),
            getDriverResultsBySeasonUseCase = GetDriverResultsBySeasonUseCase(fakeRepository),
            getConstructorDriversBySeasonUseCase = GetConstructorDriversBySeasonUseCase(fakeRepository),
            ioDispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectTab(F1Tab.DRIVERS)
        val state = viewModel.uiState.value as F1UiState.Success
        assertEquals(F1Tab.DRIVERS, state.selectedTab)
    }
}