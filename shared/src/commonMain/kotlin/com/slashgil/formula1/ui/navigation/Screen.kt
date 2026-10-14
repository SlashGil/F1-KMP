package com.slashgil.formula1.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object DriverDetail : Screen("driver_detail/{driverId}") {
        fun createRoute(driverId: String) = "driver_detail/$driverId"
    }
    object ConstructorDetail : Screen("constructor_detail/{constructorId}") {
        fun createRoute(constructorId: String) = "constructor_detail/$constructorId"
    }
    object RaceDetail : Screen("race_detail/{season}/{round}") {
        fun createRoute(season: String, round: Int) = "race_detail/$season/$round"
    }
}