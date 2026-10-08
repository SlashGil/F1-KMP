package com.slashgil.formula1.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    val driverId: String,
    val permanentNumber: String? = null,
    val code: String? = null,
    val url: String = "",
    val givenName: String,
    val familyName: String,
    val dateOfBirth: String = "",
    val nationality: String = ""
) {
    val fullName: String
        get() = "$givenName $familyName"
}