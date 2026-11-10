package com.kamildex.kbinchecker.data

import android.content.Context
import com.kamildex.kbinchecker.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import java.io.File

/**
 * Lookup history kept in a small JSON file inside the app's private storage.
 * Nothing here ever leaves the device. Newest entries come first.
 */
class HistoryStore(context: Context) {

    private val file = File(context.filesDir, "history.json")
    private val lock = Any()

    private val _items = MutableStateFlow<List<BinInfo>>(emptyList())
    val items: StateFlow<List<BinInfo>> = _items.asStateFlow()

    /** Reads the file. Call once from a background thread. */
    fun load() {
        synchronized(lock) {
            _items.value = try {
                if (!file.exists()) {
                    emptyList()
                } else {
                    val array = JSONArray(file.readText())
                    val list = ArrayList<BinInfo>(array.length())
                    for (i in 0 until array.length()) {
                        val item = BinInfo.fromJson(array.getJSONObject(i))
                        if (item.bin.isNotEmpty()) list.add(item)
                    }
                    list
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun find(bin: String): BinInfo? = _items.value.firstOrNull { it.bin == bin }

    /** Inserts or replaces the entry for this BIN and moves it to the top. */
    fun upsert(info: BinInfo) = update { current ->
        (listOf(info) + current.filter { it.bin != info.bin }).take(AppConfig.HISTORY_LIMIT)
    }

    fun setFavorite(bin: String, favorite: Boolean) = update { current ->
        current.map { if (it.bin == bin) it.copy(favorite = favorite) else it }
    }

    fun delete(bin: String) = update { current -> current.filter { it.bin != bin } }

    fun clear() = update { emptyList() }

    private fun update(transform: (List<BinInfo>) -> List<BinInfo>) {
        synchronized(lock) {
            val next = transform(_items.value)
            _items.value = next
            try {
                val array = JSONArray()
                next.forEach { array.put(it.toJson()) }
                val tmp = File(file.parentFile, "history.json.tmp")
                tmp.writeText(array.toString())
                if (!tmp.renameTo(file)) {
                    file.writeText(array.toString())
                    tmp.delete()
                }
            } catch (e: Exception) {
                // Storage problems must never crash a lookup; the in-memory list stays valid.
            }
        }
    }
}
