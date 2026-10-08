package com.slashgil.formula1

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform