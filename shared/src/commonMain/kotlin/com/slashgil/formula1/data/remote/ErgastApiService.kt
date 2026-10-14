package com.slashgil.formula1.data.remote

import com.slashgil.formula1.data.remote.dto.ConstructorDataResponse
import com.slashgil.formula1.data.remote.dto.ConstructorDto
import com.slashgil.formula1.data.remote.dto.ConstructorStandingDto
import com.slashgil.formula1.data.remote.dto.DriverDataResponse
import com.slashgil.formula1.data.remote.dto.DriverDto
import com.slashgil.formula1.data.remote.dto.DriverStandingDto
import com.slashgil.formula1.data.remote.dto.ErgastResponse
import com.slashgil.formula1.data.remote.dto.LapDataResponse
import com.slashgil.formula1.data.remote.dto.LapDto
import com.slashgil.formula1.data.remote.dto.PitStopDataResponse
import com.slashgil.formula1.data.remote.dto.PitStopDto
import com.slashgil.formula1.data.remote.dto.RaceDataResponse
import com.slashgil.formula1.data.remote.dto.RaceDto
import com.slashgil.formula1.data.remote.dto.ResultDto
import com.slashgil.formula1.data.remote.dto.StandingsDataResponse
import com.slashgil.formula1.data.remote.dto.WikipediaSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface ErgastApiService {
    suspend fun getDrivers(season: String? = null): List<DriverDto>
    suspend fun getConstructors(season: String? = null): List<ConstructorDto>
    suspend fun getSchedule(season: String): List<RaceDto>
    suspend fun getRaceResults(season: String, round: Int): List<ResultDto>
    suspend fun getDriverStandings(season: String): List<DriverStandingDto>
    suspend fun getConstructorStandings(season: String): List<ConstructorStandingDto>
    suspend fun getWikipediaSummary(wikiUrl: String): WikipediaSummaryDto?
    suspend fun getPitStops(season: String, round: Int): List<PitStopDto>
    suspend fun getLapTimes(season: String, round: Int): List<LapDto>
    suspend fun getDriverResultsBySeason(season: String, driverId: String): List<RaceDto>
    suspend fun getConstructorDriversBySeason(season: String, constructorId: String): List<DriverDto>
}

class ErgastApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String = "https://api.jolpi.ca/ergast/f1"
) : ErgastApiService {

    override suspend fun getDrivers(season: String?): List<DriverDto> {
        val endpoint = if (season != null) "$baseUrl/$season/drivers.json?limit=100" else "$baseUrl/drivers.json?limit=1000"
        val response = client.get(endpoint).body<ErgastResponse<DriverDataResponse>>()
        return response.mrData.driverTable?.drivers ?: emptyList()
    }

    override suspend fun getConstructors(season: String?): List<ConstructorDto> {
        val endpoint = if (season != null) "$baseUrl/$season/constructors.json?limit=100" else "$baseUrl/constructors.json?limit=1000"
        val response = client.get(endpoint).body<ErgastResponse<ConstructorDataResponse>>()
        return response.mrData.constructorTable?.constructors ?: emptyList()
    }

    override suspend fun getSchedule(season: String): List<RaceDto> {
        val response = client.get("$baseUrl/$season.json").body<ErgastResponse<RaceDataResponse>>()
        return response.mrData.raceTable?.races ?: emptyList()
    }

    override suspend fun getRaceResults(season: String, round: Int): List<ResultDto> {
        val response = client.get("$baseUrl/$season/$round/results.json").body<ErgastResponse<RaceDataResponse>>()
        return response.mrData.raceTable?.races?.firstOrNull()?.results ?: emptyList()
    }

    override suspend fun getDriverStandings(season: String): List<DriverStandingDto> {
        val response = client.get("$baseUrl/$season/driverStandings.json").body<ErgastResponse<StandingsDataResponse>>()
        return response.mrData.standingsTable?.standingsLists?.firstOrNull()?.driverStandings ?: emptyList()
    }

    override suspend fun getConstructorStandings(season: String): List<ConstructorStandingDto> {
        val response = client.get("$baseUrl/$season/constructorStandings.json").body<ErgastResponse<StandingsDataResponse>>()
        return response.mrData.standingsTable?.standingsLists?.firstOrNull()?.constructorStandings ?: emptyList()
    }

    override suspend fun getWikipediaSummary(wikiUrl: String): WikipediaSummaryDto? {
        if (wikiUrl.isBlank()) return null
        return try {
            val titleSlug = wikiUrl.substringAfterLast("/")
            if (titleSlug.isBlank()) return null
            val summaryUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/$titleSlug"
            client.get(summaryUrl).body<WikipediaSummaryDto>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPitStops(season: String, round: Int): List<PitStopDto> {
        return try {
            val response = client.get("$baseUrl/$season/$round/pitstops.json?limit=100").body<ErgastResponse<PitStopDataResponse>>()
            response.mrData.raceTable?.races?.firstOrNull()?.pitStops ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getLapTimes(season: String, round: Int): List<LapDto> {
        return try {
            val response = client.get("$baseUrl/$season/$round/laps.json?limit=2000").body<ErgastResponse<LapDataResponse>>()
            response.mrData.raceTable?.races?.firstOrNull()?.laps ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getDriverResultsBySeason(season: String, driverId: String): List<RaceDto> {
        return try {
            val response = client.get("$baseUrl/$season/drivers/$driverId/results.json").body<ErgastResponse<RaceDataResponse>>()
            response.mrData.raceTable?.races ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getConstructorDriversBySeason(season: String, constructorId: String): List<DriverDto> {
        return try {
            val response = client.get("$baseUrl/$season/constructors/$constructorId/drivers.json").body<ErgastResponse<DriverDataResponse>>()
            response.mrData.driverTable?.drivers ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}