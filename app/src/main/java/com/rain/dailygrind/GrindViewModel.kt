package com.rain.dailygrind

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rain.dailygrind.data.DailyLog
import com.rain.dailygrind.data.Defaults
import com.rain.dailygrind.data.LogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class GrindViewModel(private val repo: LogRepository) : ViewModel() {
    val isDark: StateFlow<Boolean> = repo.isDark.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = false
    )

    val coverDay: StateFlow<Int> = repo.coverDay.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), 1
    )
    val totalDays: StateFlow<Int> = repo.totalDays.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), 75
    )

    fun setCoverDay(day: Int) = viewModelScope.launch { repo.setCoverDay(day) }
    fun setTotalDays(days: Int) = viewModelScope.launch { repo.setTotalDays(days) }

    private val _log = MutableStateFlow(
        DailyLog(
            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            Defaults.checklist
        )
    )
    val log: StateFlow<DailyLog> = _log.asStateFlow()

    init {
        viewModelScope.launch {
            _log.value = repo.loadToday()
            // Autosave: sync draft every change, DataStore after short debounce
            _log.drop(1).collect { current ->
                withContext(Dispatchers.IO) { repo.saveDraftSync(current) }
            }
        }
        viewModelScope.launch {
            _log.drop(1).debounce(400.milliseconds).collect { current ->
                repo.save(current)
            }
        }
    }

    fun toggleDark() = viewModelScope.launch {
        repo.setDark(!isDark.value)
    }

    fun toggleItem(id: Int) = update { current ->
        current.copy(items = current.items.map {
            if (it.id == id) it.copy(isChecked = !it.isChecked) else it
        })
    }

    fun setDate(date: LocalDate) = update { current ->
        val str = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        current.copy(date = str, dateManual = str != today)
    }

    fun toggleSub(id: Int, subject: String) = update { current ->
        current.copy(items = current.items.map { item ->
            if (item.id != id) return@map item
            val subs = if (subject in item.subChecked) item.subChecked - subject
            else item.subChecked + subject
            // "any 2" target — main box auto-follows sub count
            item.copy(subChecked = subs, isChecked = subs.size >= 2)
        })
    }

    fun setDuration(id: Int, duration: String) = update { current ->
        current.copy(items = current.items.map {
            if (it.id == id) it.copy(duration = duration.take(12).ifBlank { null }) else it
        })
    }

    fun setNote(id: Int, note: String) = update { current ->
        current.copy(items = current.items.map {
            if (it.id == id) it.copy(note = note.take(60)) else it
        })
    }

    fun setBullet(index: Int, text: String) = update { current ->
        val bullets = current.whatIDid.toMutableList()
        if (index in bullets.indices) bullets[index] = text
        current.copy(whatIDid = bullets)
    }

    /** Call on pause/stop — flush pending draft before process die. */
    fun flushDraft() {
        repo.saveDraftSync(_log.value)
        viewModelScope.launch { repo.save(_log.value) }
    }

    override fun onCleared() {
        repo.saveDraftSync(_log.value)
        super.onCleared()
    }

    private fun update(block: (DailyLog) -> DailyLog) {
        _log.update(block)
    }

    class Factory(private val repo: LogRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GrindViewModel(repo) as T
    }
}
