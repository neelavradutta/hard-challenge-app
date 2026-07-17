package com.rain.dailygrind.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rain.dailygrind.R

// export canvas matches image ratio (682x1024) — no crop, full poster kept
const val COVER_W = 1080
const val COVER_H = 1620

private val PatchCream = Color(0xFFF0EBD9)
private val PosterGreen = Color(0xFF5A7245)

/**
 * Cover poster — uses the exact reference image as background.
 * Day number and "{total} DAYS" are patched + redrawn so they stay editable.
 * With day 23 + total 70 the original artwork shows untouched.
 */
@Composable
fun CoverCard(
    day: Int,
    totalDays: Int,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val s = width.value / 400f
    val original = day == 23 && totalDays == 70

    Box(modifier = modifier.size(width, height)) {
        Image(
            painter = painterResource(R.drawable.cover_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (!original) {
            // ── day number patch ──
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = height * 0.123f)
                    .width(width * 0.42f)
                    .height(height * 0.205f)
                    .clip(RoundedCornerShape((14 * s).dp))
                    .background(PatchCream),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    color = PosterGreen,
                    fontSize = (92 * s).sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.rotate(-4f)
                )
            }

            // ── "{total} DAYS" patch ──
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = height * 0.352f)
                    .width(width * 0.60f)
                    .height(height * 0.085f)
                    .clip(RoundedCornerShape((10 * s).dp))
                    .background(PatchCream),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$totalDays DAYS",
                    color = PosterGreen,
                    fontSize = (38 * s).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (1 * s).sp
                )
            }
        }
    }
}
