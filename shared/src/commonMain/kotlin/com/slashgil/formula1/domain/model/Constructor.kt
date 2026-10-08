package com.slashgil.formula1.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Constructor(
    val constructorId: String,
    val url: String = "",
    val name: String,
    val nationality: String = ""
)