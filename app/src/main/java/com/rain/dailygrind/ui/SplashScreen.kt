package com.rain.dailygrind.ui

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
@Composable
fun SplashScreen(dark: Boolean = false) {
    val bgTop = if (dark) Color(0xFF0B0B10) else Color(0xFFF8F7FC)
    val bgBottom = if (dark) Color(0xFF12101E) else Color(0xFFEDEAF8)
    val cyan = if (dark) Color(0xFF6FE3FF) else Color(0xFF0091AD)
    val violet = if (dark) Color(0xFFB98CFF) else Color(0xFF7A4FE0)
    val textSoft = if (dark) Color(0xFF9A9AA8) else Color(0xFF6B6B78)

    val infinite = rememberInfiniteTransition(label = "splash")

    // slow breathing of corner glows — subtle, far from center
    val breathe by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(5000, easing = LinearEasing)),
        label = "breathe"
    )
    // pulsing loader dots
    val dotPhase by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "dots"
    )

    // one-shot entrance
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val titleAlpha by animateFloatAsState(
        if (shown) 1f else 0f, tween(900, easing = EaseOutCubic), label = "ta"
    )
    val titleShift by animateFloatAsState(
        if (shown) 0f else 24f, tween(900, easing = EaseOutCubic), label = "ts"
    )
    val lineW by animateFloatAsState(
        if (shown) 88f else 0f, tween(800, delayMillis = 500, easing = EaseOutCubic), label = "lw"
    )
    val tagAlpha by animateFloatAsState(
        if (shown) 1f else 0f, tween(700, delayMillis = 750), label = "tga"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
    ) {
        // corner glows only — center stays clean
        Canvas(modifier = Modifier.fillMaxSize()) {
            val wave = (sin(breathe * 2f * PI.toFloat()) + 1f) / 2f // 0..1
            val a1 = 0.10f + 0.06f * wave
            val a2 = 0.16f - 0.06f * wave

            drawCircle(
                Brush.radialGradient(
                    listOf(cyan.copy(alpha = a1), Color.Transparent),
                    center = Offset(size.width * 0.0f, size.height * 0.0f),
                    radius = size.minDimension * 0.75f
                ),
                radius = size.minDimension * 0.75f,
                center = Offset(size.width * 0.0f, size.height * 0.0f)
            )
            drawCircle(
                Brush.radialGradient(
                    listOf(violet.copy(alpha = a2), Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.minDimension * 0.85f
                ),
                radius = size.minDimension * 0.85f,
                center = Offset(size.width, size.height)
            )
        }

        // logo block — static position, no drawing behind it
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DailyGrind",
                style = TextStyle(
                    brush = if (dark)
                        Brush.horizontalGradient(listOf(Color.White, Color(0xFFD8F6FF)))
                    else
                        Brush.horizontalGradient(listOf(Color(0xFF14141A), Color(0xFF3D2E7C))),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier
                    .alpha(titleAlpha)
                    .padding(bottom = 14.dp)
                    .offsetY(titleShift)
            )
            // accent underline grows from center
            Box(
                modifier = Modifier
                    .width(lineW.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(cyan, violet)))
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "FOCUS  •  PLAN  •  WORK  •  ACHIEVE",
                color = textSoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(tagAlpha)
            )
        }

        // three pulsing dots — bottom, far from text
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .alpha(tagAlpha),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { i ->
                val phase = (dotPhase + i * 0.25f) % 1f
                val pulse = (sin(phase * 2f * PI.toFloat()) + 1f) / 2f // 0..1
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .scale(0.7f + 0.4f * pulse)
                        .alpha(0.35f + 0.65f * pulse)
                        .clip(CircleShape)
                        .background(if (i == 1) violet else cyan)
                )
            }
        }
    }
}

private fun Modifier.offsetY(y: Float): Modifier =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, y.toInt())
        }
    }
