package com.rain.dailygrind.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rain.dailygrind.data.ChecklistItem
import com.rain.dailygrind.data.DailyLog
import com.rain.dailygrind.data.Defaults
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// ── Fixed palette from reference design (theme-independent) ──
private val BgTop = Color(0xFFD8C9F5)
private val BgMid = Color(0xFFC4B2EF)
private val BgBottom = Color(0xFFB7D4F0)
private val Navy = Color(0xFF2A2550)
private val Violet = Color(0xFF7B5CD6)
private val VioletDeep = Color(0xFF6A4BC8)
private val RowBg = Color(0xFFF7F5FC)
private val GreenOk = Color(0xFF4CAF50)
private val RedNo = Color(0xFFE53957)
private val LabelGray = Color(0xFF6B6B78)

private data class RowStyle(val icon: ImageVector, val badge: Color)

private fun rowStyle(id: Int): RowStyle = when (id) {
    1 -> RowStyle(Icons.Rounded.Code, Color(0xFF8B5CF6))
    2 -> RowStyle(Icons.Rounded.DesktopWindows, Color(0xFF3B9DF2))
    3 -> RowStyle(Icons.Rounded.Storage, Color(0xFF35B8B0))
    4 -> RowStyle(Icons.Rounded.Psychology, Color(0xFFF5A623))
    5 -> RowStyle(Icons.AutoMirrored.Rounded.MenuBook, Color(0xFFEF4D8D))
    6 -> RowStyle(Icons.Rounded.Work, Color(0xFF7C5CBF))
    7 -> RowStyle(Icons.AutoMirrored.Rounded.DirectionsWalk, Color(0xFF57B45C))
    8 -> RowStyle(Icons.Rounded.Bedtime, Color(0xFF6C3FD1))
    9 -> RowStyle(Icons.Rounded.WbSunny, Color(0xFFF7B32B))
    else -> RowStyle(Icons.Rounded.PhoneAndroid, Color(0xFF4A9FE8))
}

/**
 * Instagram export card — recreation of the reference "Today's Checklist"
 * design. All dimensions scale with [width] so preview and 1080px render match.
 */
@Composable
fun ExportCard(
    log: DailyLog,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val s = width.value / 400f
    val date = runCatching { LocalDate.parse(log.date) }.getOrElse { LocalDate.now() }
    val dateLine = date.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault()))
    val bullets = log.whatIDid.filter { it.isNotBlank() }

    Box(
        modifier = modifier
            .size(width, height)
            .background(Brush.verticalGradient(listOf(BgTop, BgMid, BgBottom)))
            .drawBehind {
                // soft organic blobs
                drawCircle(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.12f),
                        radius = size.width * 0.45f
                    ),
                    radius = size.width * 0.45f,
                    center = Offset(size.width * 0.85f, size.height * 0.12f)
                )
                drawCircle(
                    Brush.radialGradient(
                        listOf(Violet.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width * 0.05f, size.height * 0.45f),
                        radius = size.width * 0.5f
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.05f, size.height * 0.45f)
                )
                // sparkle dots — corners
                val dot = Color.White.copy(alpha = 0.55f)
                val grid = 9f * s
                for (i in 0..3) for (j in 0..2) {
                    drawCircle(dot, 1.6f * s, Offset(size.width - 60f * s + i * grid, 40f * s + j * grid))
                    drawCircle(dot, 1.6f * s, Offset(30f * s + i * grid, size.height - 60f * s + j * grid))
                }
                // four-point sparkles
                listOf(
                    Offset(size.width * 0.06f, size.height * 0.09f),
                    Offset(size.width * 0.94f, size.height * 0.30f),
                    Offset(size.width * 0.05f, size.height * 0.68f)
                ).forEach { c ->
                    val r = 7f * s
                    drawLine(Color.White.copy(alpha = 0.8f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), 2f * s)
                    drawLine(Color.White.copy(alpha = 0.8f), Offset(c.x, c.y - r), Offset(c.x, c.y + r), 2f * s)
                }
            }
            .padding(horizontal = (24 * s).dp, vertical = (22 * s).dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ───────────────────────────────────────────
            Text(
                text = "★",
                color = VioletDeep,
                fontSize = (10 * s).sp
            )
            Text(
                text = "TODAY'S",
                color = Navy,
                fontSize = (29 * s).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (1.5f * s).sp
            )
            Text(
                text = "~ Checklist ~",
                color = VioletDeep,
                fontSize = (24 * s).sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Cursive
            )
            Spacer(Modifier.height((6 * s).dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Violet)
                    .padding(horizontal = (12 * s).dp, vertical = (4 * s).dp)
            ) {
                Text(
                    text = "DISCIPLINE TODAY • SUCCESS TOMORROW",
                    color = Color.White,
                    fontSize = (9 * s).sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (1 * s).sp
                )
            }
            Spacer(Modifier.height((4 * s).dp))
            Text(
                text = dateLine,
                color = Navy.copy(alpha = 0.65f),
                fontSize = (10 * s).sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height((8 * s).dp))

            // ── Checklist rows — each row shares space equally,
            //    so any item count always fits the card ──────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy((4 * s).dp)
            ) {
                log.items.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        ChecklistExportRow(item, s)
                    }
                }
            }

            Spacer(Modifier.height((8 * s).dp))

            // ── What I Did box ───────────────────────────────────
            Box(contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = (10 * s).dp)
                        .clip(RoundedCornerShape((14 * s).dp))
                        .background(Color.White.copy(alpha = 0.55f))
                        .border(
                            (1.2f * s).dp,
                            Violet.copy(alpha = 0.45f),
                            RoundedCornerShape((14 * s).dp)
                        )
                        .padding(
                            start = (16 * s).dp,
                            end = (16 * s).dp,
                            top = (16 * s).dp,
                            bottom = (12 * s).dp
                        )
                ) {
                    val lines = listOf(
                        bullets.getOrElse(0) { "" },
                        bullets.getOrElse(1) { "" }
                    )
                    lines.forEachIndexed { i, line ->
                        Column(modifier = Modifier.padding(bottom = (6 * s).dp)) {
                            Text(
                                text = if (line.isBlank()) "" else "${i + 1}. $line",
                                color = Navy,
                                fontSize = (11.5f * s).sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height((3 * s).dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((1 * s).dp)
                                    .background(Violet.copy(alpha = 0.35f))
                            )
                        }
                    }
                }
                // pill title overlapping box top edge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Violet)
                        .padding(horizontal = (16 * s).dp, vertical = (4.5f * s).dp)
                ) {
                    Text(
                        text = "WHAT I DID TODAY",
                        color = Color.White,
                        fontSize = (10 * s).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (1.5f * s).sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ChecklistExportRow(item: ChecklistItem, s: Float) {
    val style = rowStyle(item.id)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = (8 * s).dp)
            .clip(RoundedCornerShape((9 * s).dp))
            .background(RowBg)
            .padding(horizontal = (9 * s).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size((18 * s).dp)
                .clip(RoundedCornerShape((5.5f * s).dp))
                .background(style.badge),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((12 * s).dp)
            )
        }
        Spacer(Modifier.width((8 * s).dp))
        Text(
            text = if (item.subChecked.isEmpty()) item.label.substringBefore(" (")
            else "${item.label.substringBefore(" (")} → ${item.subChecked.joinToString(", ")}",
            color = Navy,
            fontSize = (11.5f * s).sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (item.duration != null) {
            Spacer(Modifier.width((4 * s).dp))
            Text(
                text = when (item.id) {
                    Defaults.SLEEP_ID, Defaults.WAKE_ID, Defaults.SCREEN_ID -> item.duration
                    else -> "(${item.duration})"
                },
                color = LabelGray,
                fontSize = (9.5f * s).sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
        if (item.note.isNotBlank()) {
            Spacer(Modifier.width((6 * s).dp))
            Text(
                text = item.note,
                color = LabelGray,
                fontSize = (9.5f * s).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.width((6 * s).dp))
        Box(
            modifier = Modifier
                .size((15 * s).dp)
                .clip(CircleShape)
                .background(if (item.isChecked) GreenOk else RedNo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (item.isChecked) Icons.Rounded.Check else Icons.Rounded.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((10 * s).dp)
            )
        }
    }
}
