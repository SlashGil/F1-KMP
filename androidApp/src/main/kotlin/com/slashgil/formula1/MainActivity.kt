package com.slashgil.formula1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

        val isDev = BuildConfig.FLAVOR == "dev" || BuildConfig.DEBUG

        setContent {
            App(
                isDevFlavor = isDev,
                logoContent = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_f1_app_logo),
                        contentDescription = "F1 Logo",
                        modifier = Modifier.size(140.dp)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}