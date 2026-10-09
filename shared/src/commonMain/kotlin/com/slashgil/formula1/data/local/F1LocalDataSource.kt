package com.slashgil.formula1.data.local

import com.slashgil.formula1.db.F1Database
import com.slashgil.formula1.domain.model.Circuit
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.Race

class F1LocalDataSource(database: F1Database) {
    private val queries = database.f1DatabaseQueries

    fun getAllDrivers(): List<Driver> {
        return queries.selectAllDrivers().executeAsList().map { entity ->
            Driver(
                driverId = entity.driverId,
                permanentNumber = entity.permanentNumber,
                code = entity.code,
                url = entity.url,
                givenName = entity.givenName,
                familyName = entity.familyName,
                dateOfBirth = entity.dateOfBirth,
                nationality = entity.nationality
            )
        }
    }

    fun saveDrivers(drivers: List<Driver>) {
        queries.transaction {
            drivers.forEach { driver ->
                queries.insertDriver(
                    driverId = driver.driverId,
                    permanentNumber = driver.permanentNumber,
                    code = driver.code,
                    url = driver.url,
                    givenName = driver.givenName,
                    familyName = driver.familyName,
                    dateOfBirth = driver.dateOfBirth,
                    nationality = driver.nationality
                )
            }
        }
    }

    fun getRacesBySeason(season: String): List<Race> {
        return queries.selectRacesBySeason(season).executeAsList().map { entity ->
            Race(
                season = entity.season,
                round = entity.round.toInt(),
                url = entity.url,
                raceName = entity.raceName,
                circuit = Circuit(
                    circuitId = entity.circuitId,
                    circuitName = entity.circuitName
                ),
                date = entity.date,
                time = entity.time
            )
        }
    }

    fun saveRaces(races: List<Race>) {
        queries.transaction {
            races.forEach { race ->
                queries.insertRace(
                    season = race.season,
                    round = race.round.toLong(),
                    url = race.url,
                    raceName = race.raceName,
                    circuitId = race.circuit.circuitId,
                    circuitName = race.circuit.circuitName,
                    date = race.date,
                    time = race.time
                )
            }
        }
    }

    fun getDriverStandings(season: String): List<DriverStanding> {
        return queries.selectDriverStandingsBySeason(season).executeAsList().mapNotNull { entity ->
            val driverEntity = queries.selectDriverById(entity.driverId).executeAsOneOrNull() ?: return@mapNotNull null
            DriverStanding(
                position = entity.position.toInt(),
                positionText = entity.positionText,
                points = entity.points,
                wins = entity.wins.toInt(),
                driver = Driver(
                    driverId = driverEntity.driverId,
                    permanentNumber = driverEntity.permanentNumber,
                    code = driverEntity.code,
                    url = driverEntity.url,
                    givenName = driverEntity.givenName,
                    familyName = driverEntity.familyName,
                    dateOfBirth = driverEntity.dateOfBirth,
                    nationality = driverEntity.nationality
                )
            )
        }
    }

    fun saveDriverStandings(season: String, standings: List<DriverStanding>) {
        queries.transaction {
            standings.forEach { standing ->
                val driver = standing.driver
                queries.insertDriver(
                    driverId = driver.driverId,
                    permanentNumber = driver.permanentNumber,
                    code = driver.code,
                    url = driver.url,
                    givenName = driver.givenName,
                    familyName = driver.familyName,
                    dateOfBirth = driver.dateOfBirth,
                    nationality = driver.nationality
                )
                queries.insertDriverStanding(
                    season = season,
                    driverId = driver.driverId,
                    position = standing.position.toLong(),
                    positionText = standing.positionText,
                    points = standing.points,
                    wins = standing.wins.toLong()
                )
            }
        }
    }
}