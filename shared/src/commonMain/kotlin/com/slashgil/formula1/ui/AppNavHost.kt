package com.slashgil.formula1.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.slashgil.formula1.ui.navigation.Screen
import com.slashgil.formula1.ui.screens.ConstructorDetailScreen
import com.slashgil.formula1.ui.screens.DriverDetailScreen
import com.slashgil.formula1.ui.screens.HomeScreen
import com.slashgil.formula1.ui.screens.RaceDetailScreen
import com.slashgil.formula1.ui.screens.SplashScreen

@Composable
fun AppNavHost(
    viewModel: F1ViewModel,
    modifier: Modifier = Modifier,
    isDevFlavor: Boolean = false,
    logoContent: (@Composable () -> Unit)? = null,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isDevFlavor = isDevFlavor,
                logoContent = logoContent,
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onDriverClick = { driver ->
                    navController.navigate(Screen.DriverDetail.createRoute(driver.driverId))
                },
                onConstructorClick = { constructor ->
                    navController.navigate(Screen.ConstructorDetail.createRoute(constructor.constructorId))
                },
                onRaceClick = { race ->
                    navController.navigate(Screen.RaceDetail.createRoute(race.season, race.round))
                }
            )
        }

        composable(
            route = Screen.DriverDetail.route,
            arguments = listOf(navArgument("driverId") { type = NavType.StringType })
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.read { getString("driverId") } ?: ""
            DriverDetailScreen(
                driverId = driverId,
                viewModel = viewModel,
                onRaceClick = { race ->
                    navController.navigate(Screen.RaceDetail.createRoute(race.season, race.round))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ConstructorDetail.route,
            arguments = listOf(navArgument("constructorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val constructorId = backStackEntry.arguments?.read { getString("constructorId") } ?: ""
            ConstructorDetailScreen(
                constructorId = constructorId,
                viewModel = viewModel,
                onDriverClick = { driver ->
                    navController.navigate(Screen.DriverDetail.createRoute(driver.driverId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.RaceDetail.route,
            arguments = listOf(
                navArgument("season") { type = NavType.StringType },
                navArgument("round") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val season = backStackEntry.arguments?.read { getString("season") } ?: "2024"
            val round = backStackEntry.arguments?.read { getInt("round") } ?: 1
            RaceDetailScreen(
                season = season,
                round = round,
                viewModel = viewModel,
                onDriverClick = { driver ->
                    navController.navigate(Screen.DriverDetail.createRoute(driver.driverId))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}