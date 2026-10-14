package com.slashgil.formula1.domain.repository

import com.slashgil.formula1.data.remote.dto.WikipediaSummaryDto
import com.slashgil.formula1.domain.model.Constructor
import com.slashgil.formula1.domain.model.ConstructorStanding
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.LapTiming
import com.slashgil.formula1.domain.model.PitStop
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.model.RaceResult

interface F1Repository {
    suspend fun getDrivers(season: String? = null): Result<List<Driver>>
    suspend fun getDriverDetails(driverId: String): Result<Driver>
    suspend fun getConstructors(season: String? = null): Result<List<Constructor>>
    suspend fun getSchedule(season: String): Result<List<Race>>
    suspend fun getRaceResults(season: String, round: Int): Result<List<RaceResult>>
    suspend fun getDriverStandings(season: String): Result<List<DriverStanding>>
    suspend fun getConstructorStandings(season: String): Result<List<ConstructorStanding>>
    suspend fun getWikipediaSummary(wikiUrl: String): Result<WikipediaSummaryDto>
    suspend fun getPitStops(season: String, round: Int): Result<List<PitStop>>
    suspend fun getLapTimes(season: String, round: Int): Result<List<LapTiming>>
    suspend fun getDriverResultsBySeason(season: String, driverId: String): Result<List<Race>>
    suspend fun getConstructorDriversBySeason(season: String, constructorId: String): Result<List<Driver>>
}