package com.rain.dailygrind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rain.dailygrind.data.LogRepository
import com.rain.dailygrind.ui.HomeScreen
import com.rain.dailygrind.ui.SplashScreen
import com.rain.dailygrind.ui.theme.DailyGrindTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val vm: GrindViewModel by viewModels {
        GrindViewModel.Factory(LogRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dark by vm.isDark.collectAsState()
            var showSplash by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                delay(2500)
                showSplash = false
            }
            DailyGrindTheme(dark = dark) {
                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(500),
                    label = "splash"
                ) { splash ->
                    if (splash) {
                        SplashScreen(dark = dark)
                    } else {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            HomeScreen(vm)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        vm.flushDraft()
        super.onPause()
    }

    override fun onStop() {
        vm.flushDraft()
        super.onStop()
    }
}
