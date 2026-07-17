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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── palette from reference poster ──
private val Cream = Color(0xFFF3EFDF)
private val CreamDeep = Color(0xFFE9E4CE)
private val GreenInk = Color(0xFF556B3B)
private val GreenDark = Color(0xFF4A5F33)
private val GreenSoft = Color(0xFFA8B98A)
private val GreenPale = Color(0xFFCBD5AE)
private val PaperWhite = Color(0xFFFBFAF2)

/**
 * "X DAYS HARD CHALLENGE" cover poster — recreation of reference image.
 * Scales with [width]; day + totalDays dynamic.
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

    Box(
        modifier = modifier
            .size(width, height)
            .background(Brush.verticalGradient(listOf(Cream, CreamDeep)))
            .drawBehind {
                // soft leafy shadow blobs
                drawCircle(
                    Brush.radialGradient(
                        listOf(GreenSoft.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(size.width * 0.95f, size.height * 0.05f),
                        radius = size.width * 0.45f
                    ),
                    radius = size.width * 0.45f,
                    center = Offset(size.width * 0.95f, size.height * 0.05f)
                )
                drawCircle(
                    Brush.radialGradient(
                        listOf(GreenSoft.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width * 0.02f, size.height * 0.55f),
                        radius = size.width * 0.5f
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.02f, size.height * 0.55f)
                )
                // dot grid top-left
                val dotC = GreenInk.copy(alpha = 0.55f)
                for (i in 0..3) for (j in 0..2) {
                    drawCircle(
                        dotC, 2.2f * s,
                        Offset((26 + i * 11) * s, (26 + j * 11) * s)
                    )
                }
                // leaves top-right (simple ovals rotated)
                val leaf = GreenInk.copy(alpha = 0.75f)
                listOf(
                    Triple(0.88f, 0.03f, 35f),
                    Triple(0.95f, 0.06f, -20f),
                    Triple(0.90f, 0.10f, 60f)
                ).forEach { (fx, fy, _) ->
                    drawOval(
                        leaf,
                        topLeft = Offset(size.width * fx, size.height * fy),
                        size = androidx.compose.ui.geometry.Size(26f * s, 12f * s)
                    )
                }
            }
            // thin frame
            .padding((10 * s).dp)
            .border((1.2f * s).dp, GreenInk.copy(alpha = 0.35f), RoundedCornerShape((6 * s).dp))
            .padding(horizontal = (18 * s).dp, vertical = (16 * s).dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height((6 * s).dp))
            // ~ DAY ~
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("༄", color = GreenInk, fontSize = (13 * s).sp)
                Spacer(Modifier.width((8 * s).dp))
                Text(
                    "DAY",
                    color = GreenDark,
                    fontSize = (22 * s).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (4 * s).sp
                )
                Spacer(Modifier.width((8 * s).dp))
                Text("༄", color = GreenInk, fontSize = (13 * s).sp, modifier = Modifier.rotate(180f))
            }

            // big day number
            Text(
                text = day.toString(),
                color = GreenInk,
                fontSize = (95 * s).sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.rotate(-3f)
            )
            // brush swoosh under number
            Box(
                modifier = Modifier
                    .width((150 * s).dp)
                    .height((8 * s).dp)
                    .rotate(-2f)
                    .clip(RoundedCornerShape(50))
                    .background(GreenInk.copy(alpha = 0.85f))
            )

            Spacer(Modifier.height((16 * s).dp))
            Text(
                text = "$totalDays DAYS",
                color = GreenInk,
                fontSize = (34 * s).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (1 * s).sp
            )
            Text(
                text = "HARD",
                color = GreenDark,
                fontSize = (44 * s).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (2 * s).sp
            )
            // CHALLENGE on brush stroke
            Box(
                modifier = Modifier
                    .rotate(-1.5f)
                    .clip(RoundedCornerShape((5 * s).dp))
                    .background(GreenInk)
                    .padding(horizontal = (20 * s).dp, vertical = (2 * s).dp)
            ) {
                Text(
                    text = "Challenge",
                    color = Cream,
                    fontSize = (30 * s).sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Cursive
                )
            }

            Spacer(Modifier.height((14 * s).dp))
            // tagline pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(GreenPale.copy(alpha = 0.9f))
                    .padding(horizontal = (16 * s).dp, vertical = (5 * s).dp)
            ) {
                Text(
                    text = "FOCUS • DISCIPLINE • GROWTH",
                    color = GreenDark,
                    fontSize = (11 * s).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (1.5f * s).sp
                )
            }

            Spacer(Modifier.weight(1f))

            // ── bottom scene: notebook + side cards ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                // mug + sticky column
                Column(modifier = Modifier.weight(0.62f)) {
                    Box(
                        modifier = Modifier
                            .rotate(-4f)
                            .clip(RoundedCornerShape((6 * s).dp))
                            .background(GreenDark)
                            .padding(horizontal = (10 * s).dp, vertical = (7 * s).dp)
                    ) {
                        Text(
                            "PROGRESS\n— NOT —\nPERFECTION ♡",
                            color = Cream,
                            fontSize = (8.5f * s).sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = (11 * s).sp
                        )
                    }
                    Spacer(Modifier.height((8 * s).dp))
                    Box(
                        modifier = Modifier
                            .rotate(2f)
                            .clip(RoundedCornerShape((4 * s).dp))
                            .background(Color(0xFFEFE3BE))
                            .padding(horizontal = (9 * s).dp, vertical = (6 * s).dp)
                    ) {
                        Text(
                            "SMALL STEPS\nEVERY DAY,\nBIG RESULTS\nONE DAY. ♡",
                            color = Color(0xFF6B5B33),
                            fontSize = (8 * s).sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = (10.5f * s).sp
                        )
                    }
                }
                Spacer(Modifier.width((8 * s).dp))
                // notebook study plan
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(-1f)
                        .clip(RoundedCornerShape((8 * s).dp))
                        .background(PaperWhite)
                        .border((1 * s).dp, GreenSoft, RoundedCornerShape((8 * s).dp))
                        .padding((10 * s).dp)
                ) {
                    Text(
                        "Study Plan",
                        color = GreenDark,
                        fontSize = (13 * s).sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Cursive
                    )
                    Box(
                        Modifier
                            .width((70 * s).dp)
                            .height((1 * s).dp)
                            .background(GreenSoft)
                    )
                    Spacer(Modifier.height((6 * s).dp))
                    listOf("Learn", "Practice", "Revise", "Repeat").forEach { step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = (4 * s).dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size((11 * s).dp)
                                    .clip(RoundedCornerShape((2.5f * s).dp))
                                    .border((1 * s).dp, GreenInk, RoundedCornerShape((2.5f * s).dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Check, null,
                                    tint = GreenInk,
                                    modifier = Modifier.size((8 * s).dp)
                                )
                            }
                            Spacer(Modifier.width((6 * s).dp))
                            Text(
                                step,
                                color = Color(0xFF4A4A3A),
                                fontSize = (10.5f * s).sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(Modifier.width((8 * s).dp))
                // tablet + book stack
                Column(modifier = Modifier.weight(0.72f)) {
                    Box(
                        modifier = Modifier
                            .rotate(2f)
                            .clip(RoundedCornerShape((6 * s).dp))
                            .background(Color(0xFF23301A))
                            .padding(horizontal = (9 * s).dp, vertical = (7 * s).dp)
                    ) {
                        Text(
                            "FOCUS\nTODAY ༄\nSUCCESS\nTOMORROW",
                            color = GreenPale,
                            fontSize = (8.5f * s).sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = (11 * s).sp
                        )
                    }
                    Spacer(Modifier.height((7 * s).dp))
                    listOf(
                        "MINDSET" to Color(0xFF8FA26B),
                        "CONSISTENCY" to Color(0xFF6F8551),
                        "KNOWLEDGE" to Color(0xFF57703D),
                        "GROWTH" to Color(0xFF465C31)
                    ).forEach { (label, c) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = (2.5f * s).dp)
                                .clip(RoundedCornerShape((2.5f * s).dp))
                                .background(c)
                                .padding(vertical = (3 * s).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = Cream,
                                fontSize = (7.5f * s).sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (0.5f * s).sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height((10 * s).dp))
            // footer book badge
            Box(
                modifier = Modifier
                    .size((26 * s).dp)
                    .clip(CircleShape)
                    .background(GreenInk),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.MenuBook, null,
                    tint = Cream,
                    modifier = Modifier.size((14 * s).dp)
                )
            }
        }
    }
}
