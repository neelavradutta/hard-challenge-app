package com.rain.dailygrind.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rain.dailygrind.GrindViewModel
import com.rain.dailygrind.data.ChecklistItem
import com.rain.dailygrind.data.DailyLog
import com.rain.dailygrind.data.Defaults
import com.rain.dailygrind.export.EXPORT_H
import com.rain.dailygrind.export.EXPORT_W
import com.rain.dailygrind.export.cacheShareUri
import com.rain.dailygrind.export.saveToGallery
import com.rain.dailygrind.export.shareImage
import com.rain.dailygrind.ui.theme.GrindTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: GrindViewModel) {
    val colors = GrindTheme.colors
    val log by vm.log.collectAsState()
    val isDark by vm.isDark.collectAsState()
    var showExport by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedId by remember { mutableStateOf<Int?>(null) }
    val done = log.items.count { it.isChecked }
    val progress by animateFloatAsState(
        targetValue = done / 10f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress"
    )
    val dateLabel = runCatching {
        LocalDate.parse(log.date).format(
            DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault())
        )
    }.getOrElse { log.date }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateLabel,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showDatePicker = true }
            )
            DownloadButton(onClick = { showExport = true })
            IconButton(onClick = vm::toggleDark) {
                Icon(
                    imageVector = if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                    contentDescription = "Toggle theme",
                    tint = colors.textPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(colors.divider)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(colors.accent, colors.accentAlt)))
            )
        }
        Text(
            text = "$done/10 completed",
            color = colors.textSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(log.items, key = { _, item -> item.id }) { index, item ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay((index * 60).milliseconds)
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(250)) + slideInVertically(
                        animationSpec = tween(250),
                        initialOffsetY = { 12 }
                    )
                ) {
                    ChecklistRow(
                        item = item,
                        expanded = expandedId == item.id,
                        onToggle = { vm.toggleItem(item.id) },
                        onExpand = {
                            expandedId = if (expandedId == item.id) null else item.id
                        },
                        onNote = { vm.setNote(item.id, it) },
                        onToggleSub = { vm.toggleSub(item.id, it) },
                        onDuration = { vm.setDuration(item.id, it) }
                    )
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                WhatIDidSection(bullets = log.whatIDid, onChange = vm::setBullet)
            }
        }
    }

    if (showExport) {
        val coverDay by vm.coverDay.collectAsState()
        val totalDays by vm.totalDays.collectAsState()
        ExportSheet(
            log = log,
            coverDay = coverDay,
            totalDays = totalDays,
            onCoverDay = vm::setCoverDay,
            onTotalDays = vm::setTotalDays,
            onDismiss = { showExport = false }
        )
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = runCatching {
                LocalDate.parse(log.date).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }.getOrNull()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        vm.setDate(
                            Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        )
                    }
                    showDatePicker = false
                }) { Text("OK", color = colors.accent) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun DownloadButton(onClick: () -> Unit) {
    val colors = GrindTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, tween(100), label = "dl")
    Box(
        modifier = Modifier
            .scale(scale)
            .size(32.dp)
            .shadow(
                elevation = if (pressed) 2.dp else 10.dp,
                shape = CircleShape,
                spotColor = colors.accent,
                ambientColor = colors.accentAlt
            )
            .clip(CircleShape)
            .background(colors.bgSurface)
            .border(
                width = 1.5.dp,
                brush = Brush.sweepGradient(
                    listOf(colors.accent, colors.accentAlt, colors.accent)
                ),
                shape = CircleShape
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Rounded.Download,
            contentDescription = "Export",
            tint = colors.accent,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ChecklistRow(
    item: ChecklistItem,
    expanded: Boolean,
    onToggle: () -> Unit,
    onExpand: () -> Unit,
    onNote: (String) -> Unit,
    onToggleSub: (String) -> Unit,
    onDuration: (String) -> Unit
) {
    val colors = GrindTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.bgSurface)
            .border(1.dp, colors.divider, RoundedCornerShape(14.dp))
            .clickable(onClick = onExpand)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (item.isChecked) Color(0xFF4CAF50) else Color(0xFFE53957).copy(alpha = 0.12f))
                    .border(
                        width = 1.5.dp,
                        color = if (item.isChecked) Color(0xFF4CAF50) else Color(0xFFE53957).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (item.isChecked) {
                    Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Icon(
                        Icons.Rounded.Close,
                        null,
                        tint = Color(0xFFE53957),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = item.label,
                color = colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            var editHours by remember { mutableStateOf(false) }
            if (editHours) {
                val focusRequester = remember { FocusRequester() }
                var gotFocus by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(colors.accent.copy(alpha = 0.15f))
                        .border(1.dp, colors.accent.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    BasicTextField(
                        value = item.duration ?: "",
                        onValueChange = onDuration,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = colors.accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(colors.accent),
                        modifier = Modifier
                            .width(56.dp)
                            .focusRequester(focusRequester)
                            .onFocusEvent {
                                if (it.isFocused) gotFocus = true
                                else if (gotFocus) editHours = false
                            },
                        decorationBox = { inner ->
                            Box {
                                if (item.duration == null) {
                                    Text("hrs…", color = colors.accent.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                                inner()
                            }
                        }
                    )
                }
                Spacer(Modifier.width(6.dp))
            } else if (item.duration != null || expanded) {
                // tap pill itself to edit hours — row tap won't touch it
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(colors.accent.copy(alpha = 0.15f))
                        .clickable { editHours = true }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.duration ?: "+ hrs",
                        color = colors.accent.copy(alpha = if (item.duration == null) 0.6f else 1f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.width(6.dp))
            }
            Icon(
                Icons.Rounded.Edit,
                contentDescription = "Note",
                tint = if (item.note.isNotBlank()) colors.accent else colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        if (item.id == Defaults.SUBJECTS_ID) {
            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                Defaults.subjects.forEach { sub ->
                    val on = sub in item.subChecked
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (on) colors.accent else colors.accent.copy(alpha = 0.10f))
                            .border(
                                1.dp,
                                if (on) colors.accent else colors.divider,
                                RoundedCornerShape(50)
                            )
                            .clickable { onToggleSub(sub) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sub,
                            color = if (on) Color.White else colors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                }
            }
        }
        AnimatedVisibility(visible = expanded || item.note.isNotBlank()) {
            BasicTextField(
                value = item.note,
                onValueChange = onNote,
                singleLine = true,
                textStyle = TextStyle(color = colors.textPrimary, fontSize = 14.sp),
                cursorBrush = SolidColor(colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, top = 8.dp)
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            color = colors.divider,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(bottom = 4.dp),
                decorationBox = { inner ->
                    Box {
                        if (item.note.isEmpty()) {
                            Text("Notes", color = colors.textSecondary, fontSize = 14.sp)
                        }
                        inner()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WhatIDidSection(bullets: List<String>, onChange: (Int, String) -> Unit) {
    val colors = GrindTheme.colors
    val bringer = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val onFieldFocus: () -> Unit = {
        scope.launch {
            delay(300.milliseconds) // wait for keyboard animation
            bringer.bringIntoView()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringer)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.bgSurface)
            .padding(16.dp)
    ) {
        Text(
            text = "WHAT I DID TODAY",
            color = colors.textSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(12.dp))
        BulletField(
            number = 1,
            value = bullets.getOrElse(0) { "" },
            placeholder = "Thoughts on your mind",
            onChange = { onChange(0, it) },
            onFocus = onFieldFocus
        )
        AnimatedVisibility(
            visible = bullets.getOrElse(0) { "" }.isNotBlank(),
            enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 4 },
            exit = fadeOut(tween(150))
        ) {
            Column {
                Spacer(Modifier.height(10.dp))
                BulletField(
                    number = 2,
                    value = bullets.getOrElse(1) { "" },
                    placeholder = "+ add second point",
                    onChange = { onChange(1, it) },
                    onFocus = onFieldFocus
                )
            }
        }
    }
}

@Composable
private fun BulletField(
    number: Int,
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    onFocus: () -> Unit = {}
) {
    val colors = GrindTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$number.",
            color = colors.accent,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(end = 8.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = TextStyle(color = colors.textPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(colors.accent),
            modifier = Modifier
                .weight(1f)
                .onFocusEvent { if (it.isFocused) onFocus() },
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(placeholder, color = colors.textSecondary, fontSize = 15.sp)
                    inner()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportSheet(
    log: DailyLog,
    coverDay: Int,
    totalDays: Int,
    onCoverDay: (Int) -> Unit,
    onTotalDays: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GrindTheme.colors
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val layer = rememberGraphicsLayer()
    var busy by remember { mutableStateOf(false) }
    var coverSelected by remember { mutableStateOf(false) }

    val fullW = with(density) { EXPORT_W.toDp() }
    val fullH = with(density) { EXPORT_H.toDp() }

    suspend fun capture(): Bitmap =
        layer.toImageBitmap().asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Box {
            // Full-res offscreen capture target — renders whichever card is selected
            Box(
                modifier = Modifier
                    .offset((-4000).dp)
                    .size(fullW, fullH)
                    .drawWithContent {
                        layer.record { this@drawWithContent.drawContent() }
                        drawLayer(layer)
                    }
            ) {
                if (coverSelected) {
                    CoverCard(day = coverDay, totalDays = totalDays, width = fullW, height = fullH)
                } else {
                    ExportCard(log = log, width = fullW, height = fullH)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Export", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(14.dp))

                // ── left / right choice ──
                val thumbW = 150.dp
                val thumbH = thumbW * EXPORT_H / EXPORT_W
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ExportOption(
                        label = "Today's Checklist",
                        selected = !coverSelected,
                        onClick = { coverSelected = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        ExportCard(
                            log = log,
                            width = thumbW,
                            height = thumbH,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                        )
                    }
                    ExportOption(
                        label = "Cover Photo",
                        selected = coverSelected,
                        onClick = { coverSelected = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        CoverCard(
                            day = coverDay,
                            totalDays = totalDays,
                            width = thumbW,
                            height = thumbH,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                        )
                    }
                }

                // ── cover settings ──
                AnimatedVisibility(visible = coverSelected) {
                    Row(
                        modifier = Modifier.padding(top = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        NumberSetting(
                            label = "Day",
                            value = coverDay,
                            onValue = { onCoverDay(it.coerceIn(1, 999)) }
                        )
                        NumberSetting(
                            label = "Challenge days",
                            value = totalDays,
                            onValue = { onTotalDays(it.coerceIn(1, 999)) }
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = {
                            if (busy) return@TextButton
                            busy = true
                            scope.launch {
                                val name = if (coverSelected) "cover_day$coverDay" else log.date
                                val uri = saveToGallery(context, capture(), name)
                                busy = false
                                Toast.makeText(
                                    context,
                                    if (uri != null) "Saved to gallery" else "Save failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, colors.accent, RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Rounded.Download, null, tint = colors.accent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save", color = colors.accent)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Brush.linearGradient(listOf(colors.accent, colors.accentAlt)))
                            .clickable(enabled = !busy) {
                                busy = true
                                scope.launch {
                                    val name = if (coverSelected) "cover_day$coverDay" else log.date
                                    val uri = cacheShareUri(context, capture(), name)
                                    shareImage(context, uri, toInstagram = true)
                                    busy = false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Share, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ExportOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    thumb: @Composable () -> Unit
) {
    val colors = GrindTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) colors.accent.copy(alpha = 0.10f) else Color.Transparent)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) colors.accent else colors.divider,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        thumb()
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            color = if (selected) colors.accent else colors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun NumberSetting(label: String, value: Int, onValue: (Int) -> Unit) {
    val colors = GrindTheme.colors
    var text by remember(value) { mutableStateOf(value.toString()) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = colors.textSecondary, fontSize = 13.sp)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colors.bgBase)
                .border(1.dp, colors.accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { new ->
                    text = new.filter { it.isDigit() }.take(3)
                    text.toIntOrNull()?.let(onValue)
                },
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(colors.accent),
                modifier = Modifier.width(44.dp)
            )
        }
    }
}
