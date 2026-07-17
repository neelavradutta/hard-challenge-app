package com.rain.dailygrind.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore by preferencesDataStore("dailygrind")

class LogRepository(private val context: Context) {
    private val darkKey = booleanPreferencesKey("dark")
    private val logKey = stringPreferencesKey("log")
    private val draftPrefs by lazy {
        context.getSharedPreferences("dailygrind_draft", Context.MODE_PRIVATE)
    }

    private val coverDayKey = intPreferencesKey("coverDay")
    private val totalDaysKey = intPreferencesKey("totalDays")

    val isDark: Flow<Boolean> = context.dataStore.data.map { it[darkKey] ?: false }

    val coverDay: Flow<Int> = context.dataStore.data.map { it[coverDayKey] ?: 1 }
    val totalDays: Flow<Int> = context.dataStore.data.map { it[totalDaysKey] ?: 75 }

    suspend fun setCoverDay(day: Int) {
        context.dataStore.edit { it[coverDayKey] = day }
    }

    suspend fun setTotalDays(days: Int) {
        context.dataStore.edit { it[totalDaysKey] = days }
    }

    suspend fun loadToday(): DailyLog {
        val today = today()
        val fromStore = context.dataStore.data.first()[logKey]?.let { decode(it) }
        val fromDraft = draftPrefs.getString(KEY_DRAFT, null)?.let { decode(it) }
        val candidate = when {
            fromDraft != null && (fromDraft.date == today || fromDraft.dateManual) -> fromDraft
            fromStore != null && (fromStore.date == today || fromStore.dateManual) -> fromStore
            else -> null
        }
        return if (candidate != null) {
            // labels may change between app versions — always take current ones
            candidate.copy(items = candidate.items.map { item ->
                val def = Defaults.checklist.find { it.id == item.id }
                if (def != null) item.copy(label = def.label) else item
            })
        } else {
            val fresh = fresh(today)
            saveDraftSync(fresh)
            fresh
        }
    }

    suspend fun setDark(dark: Boolean) {
        context.dataStore.edit { it[darkKey] = dark }
    }

    /** Fast crash-safe write (sync). Survives kill mid-DataStore flush. */
    fun saveDraftSync(log: DailyLog) {
        draftPrefs.edit().putString(KEY_DRAFT, encode(log)).commit()
    }

    suspend fun save(log: DailyLog) {
        saveDraftSync(log)
        context.dataStore.edit { it[logKey] = encode(log) }
    }

    private fun today() = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    private fun fresh(date: String) = DailyLog(date, Defaults.checklist)

    private fun encode(log: DailyLog): String {
        val items = JSONArray()
        log.items.forEach { item ->
            items.put(
                JSONObject()
                    .put("id", item.id)
                    .put("label", item.label)
                    .put("duration", item.duration)
                    .put("checked", item.isChecked)
                    .put("note", item.note)
                    .put("subChecked", JSONArray(item.subChecked))
            )
        }
        val bullets = JSONArray()
        log.whatIDid.forEach { bullets.put(it) }
        return JSONObject()
            .put("date", log.date)
            .put("dateManual", log.dateManual)
            .put("items", items)
            .put("whatIDid", bullets)
            .toString()
    }

    private fun decode(raw: String): DailyLog {
        val obj = JSONObject(raw)
        val itemsJson = obj.getJSONArray("items")
        val items = buildList {
            for (i in 0 until itemsJson.length()) {
                val o = itemsJson.getJSONObject(i)
                add(
                    ChecklistItem(
                        id = o.getInt("id"),
                        label = o.getString("label"),
                        duration = if (o.isNull("duration")) null else o.getString("duration"),
                        isChecked = o.getBoolean("checked"),
                        note = o.optString("note", ""),
                        subChecked = o.optJSONArray("subChecked")?.let { arr ->
                            buildList { for (j in 0 until arr.length()) add(arr.getString(j)) }
                        } ?: emptyList()
                    )
                )
            }
        }
        val bulletsJson = obj.optJSONArray("whatIDid") ?: JSONArray()
        val bullets = mutableListOf("", "")
        for (i in 0 until minOf(2, bulletsJson.length())) {
            bullets[i] = bulletsJson.getString(i)
        }
        return DailyLog(
            obj.getString("date"),
            items,
            bullets,
            obj.optBoolean("dateManual", false)
        )
    }

    companion object {
        private const val KEY_DRAFT = "draft_json"
    }
}
