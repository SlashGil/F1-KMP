package com.slashgil.formula1.data.remote.dto

import com.slashgil.formula1.domain.model.Circuit
import com.slashgil.formula1.domain.model.Constructor
import com.slashgil.formula1.domain.model.ConstructorStanding
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.FastestLap
import com.slashgil.formula1.domain.model.Location
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.domain.model.RaceResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErgastResponse<T>(
    @SerialName("MRData") val mrData: T
)

@Serializable
data class DriverDataResponse(
    @SerialName("DriverTable") val driverTable: DriverTableDto? = null
)

@Serializable
data class DriverTableDto(
    val season: String? = null,
    @SerialName("Drivers") val drivers: List<DriverDto> = emptyList()
)

@Serializable
data class DriverDto(
    val driverId: String,
    val permanentNumber: String? = null,
    val code: String? = null,
    val url: String = "",
    val givenName: String = "",
    val familyName: String = "",
    val dateOfBirth: String = "",
    val nationality: String = ""
) {
    fun toDomain(): Driver = Driver(
        driverId = driverId,
        permanentNumber = permanentNumber,
        code = code,
        url = url,
        givenName = givenName,
        familyName = familyName,
        dateOfBirth = dateOfBirth,
        nationality = nationality
    )
}

@Serializable
data class ConstructorDataResponse(
    @SerialName("ConstructorTable") val constructorTable: ConstructorTableDto? = null
)

@Serializable
data class ConstructorTableDto(
    val season: String? = null,
    @SerialName("Constructors") val constructors: List<ConstructorDto> = emptyList()
)

@Serializable
data class ConstructorDto(
    val constructorId: String,
    val url: String = "",
    val name: String = "",
    val nationality: String = ""
) {
    fun toDomain(): Constructor = Constructor(
        constructorId = constructorId,
        url = url,
        name = name,
        nationality = nationality
    )
}

@Serializable
data class RaceDataResponse(
    @SerialName("RaceTable") val raceTable: RaceTableDto? = null
)

@Serializable
data class RaceTableDto(
    val season: String? = null,
    val round: String? = null,
    @SerialName("Races") val races: List<RaceDto> = emptyList()
)

@Serializable
data class RaceDto(
    val season: String = "",
    val round: String = "0",
    val url: String = "",
    val raceName: String = "",
    @SerialName("Circuit") val circuit: CircuitDto? = null,
    val date: String = "",
    val time: String? = null,
    @SerialName("Results") val results: List<ResultDto>? = null
) {
    fun toDomain(): Race = Race(
        season = season,
        round = round.toIntOrNull() ?: 0,
        url = url,
        raceName = raceName,
        circuit = circuit?.toDomain() ?: Circuit("unknown", "", "Unknown Circuit"),
        date = date,
        time = time,
        results = results?.map { it.toDomain() }
    )
}

@Serializable
data class CircuitDto(
    val circuitId: String = "",
    val url: String = "",
    val circuitName: String = "",
    @SerialName("Location") val location: LocationDto? = null
) {
    fun toDomain(): Circuit = Circuit(
        circuitId = circuitId,
        url = url,
        circuitName = circuitName,
        location = location?.toDomain() ?: Location()
    )
}

@Serializable
data class LocationDto(
    val lat: String? = null,
    val long: String? = null,
    val locality: String = "",
    val country: String = ""
) {
    fun toDomain(): Location = Location(
        lat = lat?.toDoubleOrNull(),
        long = long?.toDoubleOrNull(),
        locality = locality,
        country = country
    )
}

@Serializable
data class ResultDto(
    val number: String = "",
    val position: String = "0",
    val positionText: String = "",
    val points: String = "0",
    @SerialName("Driver") val driver: DriverDto? = null,
    @SerialName("Constructor") val constructor: ConstructorDto? = null,
    val grid: String = "0",
    val laps: String = "0",
    val status: String = "",
    @SerialName("FastestLap") val fastestLap: FastestLapDto? = null
) {
    fun toDomain(): RaceResult = RaceResult(
        number = number,
        position = position.toIntOrNull() ?: 0,
        positionText = positionText,
        points = points.toDoubleOrNull() ?: 0.0,
        driver = driver?.toDomain() ?: Driver("unknown", givenName = "Unknown", familyName = "Driver"),
        constructor = constructor?.toDomain() ?: Constructor("unknown", name = "Unknown"),
        grid = grid.toIntOrNull() ?: 0,
        laps = laps.toIntOrNull() ?: 0,
        status = status,
        fastestLap = fastestLap?.toDomain()
    )
}

@Serializable
data class FastestLapDto(
    val rank: String? = null,
    val lap: String? = null,
    @SerialName("Time") val time: FastestLapTimeDto? = null,
    @SerialName("AverageSpeed") val averageSpeed: AverageSpeedDto? = null
) {
    fun toDomain(): FastestLap = FastestLap(
        rank = rank?.toIntOrNull(),
        lap = lap?.toIntOrNull(),
        time = time?.time,
        averageSpeed = averageSpeed?.speed
    )
}

@Serializable
data class FastestLapTimeDto(val time: String = "")

@Serializable
data class AverageSpeedDto(val speed: String = "")

@Serializable
data class StandingsDataResponse(
    @SerialName("StandingsTable") val standingsTable: StandingsTableDto? = null
)

@Serializable
data class StandingsTableDto(
    val season: String? = null,
    @SerialName("StandingsLists") val standingsLists: List<StandingsListDto> = emptyList()
)

@Serializable
data class StandingsListDto(
    val season: String = "",
    val round: String = "",
    @SerialName("DriverStandings") val driverStandings: List<DriverStandingDto> = emptyList(),
    @SerialName("ConstructorStandings") val constructorStandings: List<ConstructorStandingDto> = emptyList()
)

@Serializable
data class DriverStandingDto(
    val position: String = "0",
    val positionText: String = "",
    val points: String = "0",
    val wins: String = "0",
    @SerialName("Driver") val driver: DriverDto? = null,
    @SerialName("Constructors") val constructors: List<ConstructorDto> = emptyList()
) {
    fun toDomain(): DriverStanding = DriverStanding(
        position = position.toIntOrNull() ?: 0,
        positionText = positionText,
        points = points.toDoubleOrNull() ?: 0.0,
        wins = wins.toIntOrNull() ?: 0,
        driver = driver?.toDomain() ?: Driver("unknown", givenName = "Unknown", familyName = "Driver"),
        constructors = constructors.map { it.toDomain() }
    )
}

@Serializable
data class ConstructorStandingDto(
    val position: String = "0",
    val positionText: String = "",
    val points: String = "0",
    val wins: String = "0",
    @SerialName("Constructor") val constructor: ConstructorDto? = null
) {
    fun toDomain(): ConstructorStanding = ConstructorStanding(
        position = position.toIntOrNull() ?: 0,
        positionText = positionText,
        points = points.toDoubleOrNull() ?: 0.0,
        wins = wins.toIntOrNull() ?: 0,
        constructor = constructor?.toDomain() ?: Constructor("unknown", name = "Unknown")
    )
}