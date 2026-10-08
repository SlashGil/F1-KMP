package com.slashgil.formula1.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DriverStanding(
    val position: Int,
    val positionText: String = "",
    val points: Double = 0.0,
    val wins: Int = 0,
    val driver: Driver,
    val constructors: List<Constructor> = emptyList()
)

@Serializable
data class ConstructorStanding(
    val position: Int,
    val positionText: String = "",
    val points: Double = 0.0,
    val wins: Int = 0,
    val constructor: Constructor
)