package com.slashgil.formula1.domain.usecase

import com.slashgil.formula1.data.remote.dto.WikipediaImageDto
import com.slashgil.formula1.data.remote.dto.WikipediaSummaryDto
import com.slashgil.formula1.domain.model.Circuit
import com.slashgil.formula1.domain.model.Constructor
import com.slashgil.formula1.domain.model.ConstructorStanding
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.Location
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.model.RaceResult
import com.slashgil.formula1.domain.repository.F1Repository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeF1Repository : F1Repository {
    val sampleDriver = Driver("verstappen", "1", "VER", "url", "Max", "Verstappen", "1997-09-30", "Dutch")
    val sampleConstructor = Constructor("red_bull", "url", "Red Bull", "Austrian")
    val sampleCircuit = Circuit("red_bull_ring", "url", "Red Bull Ring", Location(47.2197, 14.7647, "Spielberg", "Austria"))
    val sampleRace = Race("2026", 1, "url", "Austrian Grand Prix", sampleCircuit, "2026-07-05")
    val sampleRaceResult = RaceResult("1", 1, "1", 25.0, sampleDriver, sampleConstructor)
    val sampleDriverStanding = DriverStanding(1, "1", 25.0, 1, sampleDriver, listOf(sampleConstructor))
    val sampleConstructorStanding = ConstructorStanding(1, "1", 25.0, 1, sampleConstructor)
    val sampleWikiSummary = WikipediaSummaryDto(
        title = "Max Verstappen",
        extract = "Max Emilian Verstappen is a Dutch racing driver.",
        thumbnail = WikipediaImageDto("https://example.com/verstappen.jpg", 320, 427)
    )

    override suspend fun getDrivers(season: String?): Result<List<Driver>> = Result.success(listOf(sampleDriver))
    override suspend fun getDriverDetails(driverId: String): Result<Driver> = Result.success(sampleDriver)
    override suspend fun getConstructors(season: String?): Result<List<Constructor>> = Result.success(listOf(sampleConstructor))
    override suspend fun getSchedule(season: String): Result<List<Race>> = Result.success(listOf(sampleRace))
    override suspend fun getRaceResults(season: String, round: Int): Result<List<RaceResult>> = Result.success(listOf(sampleRaceResult))
    override suspend fun getDriverStandings(season: String): Result<List<DriverStanding>> = Result.success(listOf(sampleDriverStanding))
    override suspend fun getConstructorStandings(season: String): Result<List<ConstructorStanding>> = Result.success(listOf(sampleConstructorStanding))
    override suspend fun getWikipediaSummary(wikiUrl: String): Result<WikipediaSummaryDto> = Result.success(sampleWikiSummary)
}

class UseCasesTest {

    private val fakeRepository = FakeF1Repository()

    @Test
    fun testGetDriversUseCase() = runTest {
        val useCase = GetDriversUseCase(fakeRepository)
        val result = useCase("2026")
        assertTrue(result.isSuccess)
        val drivers = result.getOrNull()
        assertEquals(1, drivers?.size)
        assertEquals("Max Verstappen", drivers?.first()?.fullName)
    }

    @Test
    fun testGetRaceScheduleUseCase() = runTest {
        val useCase = GetRaceScheduleUseCase(fakeRepository)
        val result = useCase("2026")
        assertTrue(result.isSuccess)
        assertEquals("Austrian Grand Prix", result.getOrNull()?.first()?.raceName)
    }

    @Test
    fun testGetDriverStandingsUseCase() = runTest {
        val useCase = GetDriverStandingsUseCase(fakeRepository)
        val result = useCase("2026")
        assertTrue(result.isSuccess)
        assertEquals(25.0, result.getOrNull()?.first()?.points)
    }

    @Test
    fun testGetConstructorStandingsUseCase() = runTest {
        val useCase = GetConstructorStandingsUseCase(fakeRepository)
        val result = useCase("2026")
        assertTrue(result.isSuccess)
        assertEquals("Red Bull", result.getOrNull()?.first()?.constructor?.name)
    }

    @Test
    fun testGetRaceResultsUseCase() = runTest {
        val useCase = GetRaceResultsUseCase(fakeRepository)
        val result = useCase("2026", 1)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.first()?.position)
    }

    @Test
    fun testGetWikipediaSummaryUseCase() = runTest {
        val useCase = GetWikipediaSummaryUseCase(fakeRepository)
        val result = useCase("http://en.wikipedia.org/wiki/Max_Verstappen")
        assertTrue(result.isSuccess)
        assertEquals("Max Verstappen", result.getOrNull()?.title)
    }
}