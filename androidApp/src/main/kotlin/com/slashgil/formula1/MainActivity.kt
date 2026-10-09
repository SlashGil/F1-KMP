package com.slashgil.formula1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.slashgil.formula1.data.local.DatabaseDriverFactory
import com.slashgil.formula1.di.initKoinWithPlatform
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (GlobalContext.getOrNull() == null) {
            val androidPlatformModule = module {
                single { DatabaseDriverFactory(applicationContext) }
            }
            initKoinWithPlatform(androidPlatformModule)
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}