package com.slashgil.formula1.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PitStop(
    val driverId: String,
    val stop: Int,
    val lap: Int,
    val time: String,
    val duration: String
)

@Serializable
data class LapTiming(
    val lapNumber: Int,
    val driverId: String,
    val position: Int,
    val time: String
)