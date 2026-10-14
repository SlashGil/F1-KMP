package com.slashgil.formula1.data.repository

import com.slashgil.formula1.data.local.F1LocalDataSource
import com.slashgil.formula1.data.remote.ErgastApiService
import com.slashgil.formula1.data.remote.dto.WikipediaSummaryDto
import com.slashgil.formula1.domain.model.Constructor
import com.slashgil.formula1.domain.model.ConstructorStanding
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.LapTiming
import com.slashgil.formula1.domain.model.PitStop
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.model.RaceResult
import com.slashgil.formula1.domain.repository.F1Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class F1RepositoryImpl(
    private val apiService: ErgastApiService,
    private val localDataSource: F1LocalDataSource
) : F1Repository {

    override suspend fun getDrivers(season: String?): Result<List<Driver>> = withContext(Dispatchers.IO) {
        try {
            val remoteDtos = apiService.getDrivers(season)
            val domainDrivers = remoteDtos.map { it.toDomain() }
            if (domainDrivers.isNotEmpty()) {
                localDataSource.saveDrivers(domainDrivers)
            }
            Result.success(domainDrivers)
        } catch (e: Exception) {
            val cachedDrivers = localDataSource.getAllDrivers()
            if (cachedDrivers.isNotEmpty()) {
                Result.success(cachedDrivers)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getDriverDetails(driverId: String): Result<Driver> = withContext(Dispatchers.IO) {
        try {
            val drivers = getDrivers().getOrNull()
            val driver = drivers?.firstOrNull { it.driverId == driverId }
            if (driver != null) {
                Result.success(driver)
            } else {
                Result.failure(NoSuchElementException("Driver $driverId not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getConstructors(season: String?): Result<List<Constructor>> = withContext(Dispatchers.IO) {
        try {
            val remoteDtos = apiService.getConstructors(season)
            Result.success(remoteDtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSchedule(season: String): Result<List<Race>> = withContext(Dispatchers.IO) {
        try {
            val remoteDtos = apiService.getSchedule(season)
            val domainRaces = remoteDtos.map { it.toDomain() }
            if (domainRaces.isNotEmpty()) {
                localDataSource.saveRaces(domainRaces)
            }
            Result.success(domainRaces)
        } catch (e: Exception) {
            val cachedRaces = localDataSource.getRacesBySeason(season)
            if (cachedRaces.isNotEmpty()) {
                Result.success(cachedRaces)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getRaceResults(season: String, round: Int): Result<List<RaceResult>> = withContext(Dispatchers.IO) {
        try {
            val remoteDtos = apiService.getRaceResults(season, round)
            Result.success(remoteDtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDriverStandings(season: String): Result<List<DriverStanding>> = withContext(Dispatchers.IO) {
        try {
            val remoteDtos = apiService.getDriverStandings(season)
            val domainStandings = remoteDtos.map { it.toDomain() }
            if (domainStandings.isNotEmpty()) {
                localDataSource.saveDriverStandings(season, domainStandings)
            }
            Result.success(domainStandings)
        } catch (e: Exception) {
            val cachedStandings = localDataSource.getDriverStandings(season)
            if (cachedStandings.isNotEmpty()) {
                Result.success(cachedStandings)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getConstructorStandings(season: String): Result<List<ConstructorStanding>> = withContext(Dispatchers.IO) {
        try {
            val remoteDtos = apiService.getConstructorStandings(season)
            Result.success(remoteDtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWikipediaSummary(wikiUrl: String): Result<WikipediaSummaryDto> = withContext(Dispatchers.IO) {
        try {
            val summary = apiService.getWikipediaSummary(wikiUrl)
            if (summary != null) {
                Result.success(summary)
            } else {
                Result.failure(IllegalStateException("No Wikipedia summary found for $wikiUrl"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPitStops(season: String, round: Int): Result<List<PitStop>> = withContext(Dispatchers.IO) {
        try {
            val dtos = apiService.getPitStops(season, round)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLapTimes(season: String, round: Int): Result<List<LapTiming>> = withContext(Dispatchers.IO) {
        try {
            val dtos = apiService.getLapTimes(season, round)
            val timings = dtos.flatMap { lapDto ->
                val lapNum = lapDto.number.toIntOrNull() ?: 0
                lapDto.timings.map { it.toDomain(lapNum) }
            }
            Result.success(timings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDriverResultsBySeason(season: String, driverId: String): Result<List<Race>> = withContext(Dispatchers.IO) {
        try {
            val dtos = apiService.getDriverResultsBySeason(season, driverId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getConstructorDriversBySeason(season: String, constructorId: String): Result<List<Driver>> = withContext(Dispatchers.IO) {
        try {
            val dtos = apiService.getConstructorDriversBySeason(season, constructorId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}