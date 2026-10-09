package com.slashgil.formula1

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.slashgil.formula1.ui.F1Screen
import com.slashgil.formula1.ui.F1ViewModel
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val viewModel: F1ViewModel = koinInject()
    F1Screen(viewModel = viewModel)
}