package com.slashgil.formula1.ui.screens

import androidx.compose.foundation.background
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
import com.slashgil.formula1.domain.model.Driver
import com.slashgil.formula1.ui.DetailItem
import com.slashgil.formula1.ui.F1UiState
import com.slashgil.formula1.ui.F1ViewModel
import com.slashgil.formula1.ui.theme.F1Dark
import com.slashgil.formula1.ui.theme.F1Red
import com.slashgil.formula1.ui.theme.F1Surface
import com.slashgil.formula1.ui.theme.F1TextPrimary
import com.slashgil.formula1.ui.theme.F1TextSecondary
import com.slashgil.formula1.ui.theme.F1Theme

@Composable
fun RaceDetailScreen(
    season: String,
    round: Int,
    viewModel: F1ViewModel,
    onDriverClick: (Driver) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState as? F1UiState.Success

    LaunchedEffect(season, round) {
        val race = state?.schedule?.firstOrNull { it.round == round }
        if (race != null) {
            viewModel.openRaceDetail(race)
        }
    }

    val detail = state?.selectedDetail as? DetailItem.RaceDetail

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
                        text = "GRAND PRIX TELEMETRY",
                        color = F1Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (detail != null) {
                    val race = detail.race
                    val wiki = detail.wikiSummary

                    val imageUrl = wiki?.thumbnail?.source ?: wiki?.originalimage?.source
                    if (imageUrl != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(F1Surface),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = race.raceName,
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
                        text = race.raceName.uppercase(),
                        color = F1TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        text = "ROUND $round // ${race.circuit.circuitName.uppercase()} // ${race.date}",
                        color = F1Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (detail.isLoadingResults) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = F1Red)
                        }
                    } else if (detail.results.isNotEmpty()) {
                        Text(text = "RACE RESULTS", color = F1TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        detail.results.forEach { result ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(F1Surface)
                                    .clickable { onDriverClick(result.driver) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "P${result.position}",
                                        color = if (result.position == 1) F1Red else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.width(32.dp)
                                    )
                                    Column {
                                        Text(
                                            text = result.driver.fullName,
                                            color = F1TextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = result.constructor.name,
                                            color = F1TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Text(
                                    text = "+${result.points} PTS",
                                    color = F1Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (wiki?.extract != null) {
                        Text(text = "CIRCUIT OVERVIEW", color = F1TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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