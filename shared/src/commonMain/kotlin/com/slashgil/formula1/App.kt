package com.slashgil.formula1

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.slashgil.formula1.ui.AppNavHost
import com.slashgil.formula1.ui.F1ViewModel
import org.koin.compose.koinInject

@Composable
@Preview
fun App(
    isDevFlavor: Boolean = false,
    logoContent: (@Composable () -> Unit)? = null
) {
    val viewModel: F1ViewModel = koinInject()
    AppNavHost(
        viewModel = viewModel,
        isDevFlavor = isDevFlavor,
        logoContent = logoContent
    )
}