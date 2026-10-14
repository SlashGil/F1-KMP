package com.slashgil.formula1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slashgil.formula1.domain.model.Constructor
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.domain.model.DriverStanding
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.ui.F1Tab
import com.slashgil.formula1.ui.F1UiState
import com.slashgil.formula1.ui.F1ViewModel
import com.slashgil.formula1.ui.theme.F1Dark
import com.slashgil.formula1.ui.theme.F1Red
import com.slashgil.formula1.ui.theme.F1Surface
import com.slashgil.formula1.ui.theme.F1SurfaceVariant
import com.slashgil.formula1.ui.theme.F1TextPrimary
import com.slashgil.formula1.ui.theme.F1TextSecondary
import com.slashgil.formula1.ui.theme.F1Theme

@Composable
fun HomeScreen(
    viewModel: F1ViewModel,
    onDriverClick: (Driver) -> Unit,
    onConstructorClick: (Constructor) -> Unit,
    onRaceClick: (Race) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    F1Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = F1Dark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                when (val state = uiState) {
                    is F1UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = F1Red)
                        }
                    }
                    is F1UiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "ERROR // TELEMETRY OFFLINE",
                                color = F1Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = F1TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadData() },
                                colors = ButtonDefaults.buttonColors(containerColor = F1Red)
                            ) {
                                Text("RETRY CONNECTION", color = Color.White)
                            }
                        }
                    }
                    is F1UiState.Success -> {
                        HeaderSection(
                            season = state.season,
                            availableSeasons = state.availableSeasons,
                            onSeasonSelected = { viewModel.selectSeason(it) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TabSelector(
                            selectedTab = state.selectedTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        when (state.selectedTab) {
                            F1Tab.STANDINGS -> StandingsTab(
                                standings = state.standings,
                                isLoading = state.isStandingsLoading,
                                onStandingClicked = { onDriverClick(it.driver) },
                                onConstructorClicked = { onConstructorClick(it) }
                            )
                            F1Tab.DRIVERS -> DriversTab(
                                drivers = state.drivers,
                                isLoading = state.isDriversLoading,
                                onDriverClicked = { onDriverClick(it) }
                            )
                            F1Tab.SCHEDULE -> ScheduleTab(
                                schedule = state.schedule,
                                isLoading = state.isScheduleLoading,
                                onRaceClicked = { onRaceClick(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(
    season: String,
    availableSeasons: List<String>,
    onSeasonSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "TELEMETRY // $season",
            color = F1TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(availableSeasons) { yr ->
                val isSelected = yr == season
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) F1Red else F1Surface)
                        .border(
                            1.dp,
                            if (isSelected) F1Red else F1SurfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSeasonSelected(yr) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = yr,
                        color = if (isSelected) Color.White else F1TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(F1Red)
        )
    }
}

@Composable
fun TabSelector(
    selectedTab: F1Tab,
    onTabSelected: (F1Tab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(F1Surface)
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        F1Tab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) F1Red else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.name,
                    color = if (isSelected) Color.White else F1TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun StandingsTab(
    standings: List<DriverStanding>,
    isLoading: Boolean,
    onStandingClicked: (DriverStanding) -> Unit,
    onConstructorClicked: (Constructor) -> Unit
) {
    if (isLoading && standings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = F1Red)
        }
    } else if (standings.isEmpty()) {
        EmptyMessage("No Standings Available")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(standings) { standing ->
                StandingCard(
                    standing = standing,
                    onStandingClicked = { onStandingClicked(standing) },
                    onConstructorClicked = { constructor -> onConstructorClicked(constructor) }
                )
            }
        }
    }
}

@Composable
fun StandingCard(
    standing: DriverStanding,
    onStandingClicked: () -> Unit,
    onConstructorClicked: (Constructor) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(F1Surface)
            .border(1.dp, F1SurfaceVariant, RoundedCornerShape(6.dp))
            .clickable { onStandingClicked() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (standing.position == 1) F1Red else F1SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = standing.position.toString().padStart(2, '0'),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = standing.driver.fullName.uppercase(),
                    color = F1TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val constructor = standing.constructors.firstOrNull()
                val teamName = constructor?.name ?: "Independent"
                Text(
                    text = teamName,
                    color = F1TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable {
                        if (constructor != null) onConstructorClicked(constructor)
                    }
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${standing.points} PTS",
                    color = F1Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "${standing.wins} WINS",
                    color = F1TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun DriversTab(
    drivers: List<Driver>,
    isLoading: Boolean,
    onDriverClicked: (Driver) -> Unit
) {
    if (isLoading && drivers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = F1Red)
        }
    } else if (drivers.isEmpty()) {
        EmptyMessage("No Drivers Available")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(drivers) { driver ->
                DriverCardItem(driver = driver, onClick = { onDriverClicked(driver) })
            }
        }
    }
}

@Composable
fun DriverCardItem(driver: Driver, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(F1Surface)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(F1Red)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = driver.permanentNumber ?: driver.code ?: "--",
                    color = F1Red,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = driver.familyName.uppercase(),
                    color = F1TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontSize = 15.sp
                )
                Text(
                    text = "${driver.givenName} // ${driver.nationality}",
                    color = F1TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ScheduleTab(
    schedule: List<Race>,
    isLoading: Boolean,
    onRaceClicked: (Race) -> Unit
) {
    if (isLoading && schedule.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = F1Red)
        }
    } else if (schedule.isEmpty()) {
        EmptyMessage("No Schedule Available")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(schedule) { race ->
                RaceCard(race = race, onClick = { onRaceClicked(race) })
            }
        }
    }
}

@Composable
fun RaceCard(race: Race, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(F1Surface)
            .border(1.dp, F1SurfaceVariant, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(F1SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "R${race.round}",
                    color = F1Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = race.raceName.uppercase(),
                    color = F1TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp
                )
                Text(
                    text = race.circuit.circuitName,
                    color = F1TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = race.date,
                color = F1TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun EmptyMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = F1TextSecondary,
            fontSize = 13.sp
        )
    }
}