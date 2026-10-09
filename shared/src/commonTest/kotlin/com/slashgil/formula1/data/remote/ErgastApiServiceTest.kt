package com.slashgil.formula1.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ErgastApiServiceTest {

    @Test
    fun testGetDriversResponseParsing() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """
                    {
                      "MRData": {
                        "series": "f1",
                        "DriverTable": {
                          "season": "2024",
                          "Drivers": [
                            {
                              "driverId": "verstappen",
                              "permanentNumber": "1",
                              "code": "VER",
                              "url": "http://en.wikipedia.org/wiki/Max_Verstappen",
                              "givenName": "Max",
                              "familyName": "Verstappen",
                              "dateOfBirth": "1997-09-30",
                              "nationality": "Dutch"
                            }
                          ]
                        }
                      }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val apiService = ErgastApiServiceImpl(httpClient)
        val drivers = apiService.getDrivers("2024")

        assertEquals(1, drivers.size)
        val driver = drivers.first().toDomain()
        assertEquals("verstappen", driver.driverId)
        assertEquals("VER", driver.code)
        assertEquals("Max Verstappen", driver.fullName)
    }

    @Test
    fun testGetScheduleResponseParsing() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """
                    {
                      "MRData": {
                        "RaceTable": {
                          "season": "2024",
                          "Races": [
                            {
                              "season": "2024",
                              "round": "1",
                              "url": "http://en.wikipedia.org/wiki/2024_Bahrain_Grand_Prix",
                              "raceName": "Bahrain Grand Prix",
                              "Circuit": {
                                "circuitId": "bahrain",
                                "url": "http://en.wikipedia.org/wiki/Bahrain_International_Circuit",
                                "circuitName": "Bahrain International Circuit",
                                "Location": {
                                  "lat": "26.0325",
                                  "long": "50.5106",
                                  "locality": "Sakhir",
                                  "country": "Bahrain"
                                }
                              },
                              "date": "2024-03-02",
                              "time": "15:00:00Z"
                            }
                          ]
                        }
                      }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val apiService = ErgastApiServiceImpl(httpClient)
        val races = apiService.getSchedule("2024")

        assertEquals(1, races.size)
        val race = races.first().toDomain()
        assertEquals("Bahrain Grand Prix", race.raceName)
        assertEquals("Bahrain International Circuit", race.circuit.circuitName)
    }
}