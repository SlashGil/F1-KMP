package com.slashgil.formula1.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val lat: Double? = null,
    val long: Double? = null,
    val locality: String = "",
    val country: String = ""
)

@Serializable
data class Circuit(
    val circuitId: String,
    val url: String = "",
    val circuitName: String,
    val location: Location = Location()
)