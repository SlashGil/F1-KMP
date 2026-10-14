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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.slashgil.formula1.domain.model.Race
import com.slashgil.formula1.ui.DetailItem
import com.slashgil.formula1.ui.F1UiState
import com.slashgil.formula1.ui.F1ViewModel
import com.slashgil.formula1.ui.components.StatBox
import com.slashgil.formula1.ui.theme.F1Dark
import com.slashgil.formula1.ui.theme.F1Red
import com.slashgil.formula1.ui.theme.F1Surface
import com.slashgil.formula1.ui.theme.F1SurfaceVariant
import com.slashgil.formula1.ui.theme.F1TextPrimary
import com.slashgil.formula1.ui.theme.F1TextSecondary
import com.slashgil.formula1.ui.theme.F1Theme

@Composable
fun DriverDetailScreen(
    driverId: String,
    viewModel: F1ViewModel,
    onRaceClick: (Race) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState as? F1UiState.Success

    LaunchedEffect(driverId) {
        val driver = state?.drivers?.firstOrNull { it.driverId == driverId }
            ?: state?.standings?.firstOrNull { it.driver.driverId == driverId }?.driver
        val standing = state?.standings?.firstOrNull { it.driver.driverId == driverId }
        if (driver != null) {
            viewModel.openDriverDetail(driver, standing)
        }
    }

    val detail = state?.selectedDetail as? DetailItem.DriverDetail

    F1Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = F1Dark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Back Button Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(F1Surface)
                            .clickable { onBack() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("< BACK", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "DRIVER PROFILE",
                        color = F1Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (detail != null) {
                    val driver = detail.driver
                    val wiki = detail.wikiSummary

                    val imageUrl = wiki?.thumbnail?.source ?: wiki?.originalimage?.source
                    if (imageUrl != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(F1Surface),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = driver.fullName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    } else if (detail.isLoadingWiki) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = F1Red)
                        }
                    }

                    Text(
                        text = driver.fullName.uppercase(),
                        color = F1TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        text = "NO. ${driver.permanentNumber ?: driver.code ?: "--"} // ${driver.nationality.uppercase()}",
                        color = F1Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (detail.standing != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(label = "POSITION", value = "P${detail.standing.position}")
                            StatBox(label = "POINTS", value = "${detail.standing.points}")
                            StatBox(label = "WINS", value = "${detail.standing.wins}")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Season Race Results History
                    if (detail.isLoadingRaces) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = F1Red)
                        }
                    } else if (detail.seasonRaces.isNotEmpty()) {
                        Text(text = "SEASON RACE RESULTS", color = F1TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        detail.seasonRaces.forEach { race ->
                            val raceResult = race.results?.firstOrNull()
                            val posStr = if (raceResult != null) "P${raceResult.position}" else "R${race.round}"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(F1Surface)
                                    .border(1.dp, F1SurfaceVariant, RoundedCornerShape(6.dp))
                                    .clickable { onRaceClick(race) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = posStr,
                                        color = if (raceResult?.position == 1) F1Red else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.width(36.dp)
                                    )
                                    Column {
                                        Text(
                                            text = race.raceName.uppercase(),
                                            color = F1TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "${race.circuit.circuitName} // ${race.date}",
                                            color = F1TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                if (raceResult != null) {
                                    Text(
                                        text = "+${raceResult.points} PTS",
                                        color = F1Red,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (wiki?.extract != null) {
                        Text(text = "BIOGRAPHY", color = F1TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = wiki.extract, color = F1TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = F1Red)
                    }
                }
            }
        }
    }
}