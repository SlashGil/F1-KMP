package com.slashgil.formula1.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Race(
    val season: String,
    val round: Int,
    val url: String = "",
    val raceName: String,
    val circuit: Circuit,
    val date: String,
    val time: String? = null,
    val results: List<RaceResult>? = null
)

@Serializable
data class RaceResult(
    val number: String = "",
    val position: Int,
    val positionText: String = "",
    val points: Double = 0.0,
    val driver: Driver,
    val constructor: Constructor,
    val grid: Int = 0,
    val laps: Int = 0,
    val status: String = "",
    val fastestLap: FastestLap? = null
)

@Serializable
data class FastestLap(
    val rank: Int? = null,
    val lap: Int? = null,
    val time: String? = null,
    val averageSpeed: String? = null
)