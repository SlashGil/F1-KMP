package com.slashgil.formula1.data.repository

import com.slashgil.formula1.data.local.F1LocalDataSource
import com.slashgil.formula1.data.remote.ErgastApiService
import com.slashgil.formula1.domain.model.Constructor
import com.slashgil.formula1.domain.model.ConstructorStanding
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.model.RaceResult
import com.slashgil.formula1.domain.repository.F1Repository

class F1RepositoryImpl(
    private val apiService: ErgastApiService,
    private val localDataSource: F1LocalDataSource
) : F1Repository {

    override suspend fun getDrivers(season: String?): Result<List<Driver>> {
        return try {
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

    override suspend fun getDriverDetails(driverId: String): Result<Driver> {
        return try {
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

    override suspend fun getConstructors(season: String?): Result<List<Constructor>> {
        return try {
            val remoteDtos = apiService.getConstructors(season)
            Result.success(remoteDtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSchedule(season: String): Result<List<Race>> {
        return try {
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

    override suspend fun getRaceResults(season: String, round: Int): Result<List<RaceResult>> {
        return try {
            val remoteDtos = apiService.getRaceResults(season, round)
            Result.success(remoteDtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDriverStandings(season: String): Result<List<DriverStanding>> {
        return try {
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

    override suspend fun getConstructorStandings(season: String): Result<List<ConstructorStanding>> {
        return try {
            val remoteDtos = apiService.getConstructorStandings(season)
            Result.success(remoteDtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}