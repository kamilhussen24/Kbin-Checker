package com.kamildex.kbinchecker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kamildex.kbinchecker.AppConfig
import com.kamildex.kbinchecker.BuildConfig
import com.kamildex.kbinchecker.data.BinApi
import com.kamildex.kbinchecker.data.BinInfo
import com.kamildex.kbinchecker.data.BinInput
import com.kamildex.kbinchecker.data.HistoryStore
import com.kamildex.kbinchecker.data.LookupResult
import com.kamildex.kbinchecker.data.Settings
import com.kamildex.kbinchecker.data.SettingsStore
import com.kamildex.kbinchecker.data.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class LookupError(val title: String, val message: String) {
    INVALID(
        "Enter 6 to 8 digits",
        "A BIN is the first 6 to 8 digits of a card number."
    ),
    NOT_FOUND(
        "BIN not found",
        "No issuer record matches this BIN. Check the digits and try again."
    ),
    NO_NETWORK(
        "No connection",
        "Check your internet connection and try again."
    ),
    RATE_LIMIT(
        "Too many requests",
        "The lookup service is busy right now. Please try again in a moment."
    ),
    SERVER(
        "Something went wrong",
        "The lookup service did not respond correctly. Please try again later."
    )
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val historyStore = HistoryStore(application)
    private val settingsStore = SettingsStore(application)

    val history: StateFlow<List<BinInfo>> = historyStore.items
    val settings: StateFlow<Settings> = settingsStore.settings

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _result = MutableStateFlow<BinInfo?>(null)
    val result: StateFlow<BinInfo?> = _result.asStateFlow()

    private val _error = MutableStateFlow<LookupError?>(null)
    val error: StateFlow<LookupError?> = _error.asStateFlow()

    private var lookupJob: Job? = null
    private var requestId = 0

    init {
        viewModelScope.launch(Dispatchers.IO) { historyStore.load() }
    }

    fun onQueryChange(raw: String) {
        val clean = BinInput.sanitize(raw)
        if (clean == _query.value) return
        _query.value = clean
        _error.value = null
    }

    fun clearQuery() {
        requestId++
        lookupJob?.cancel()
        _query.value = ""
        _result.value = null
        _error.value = null
        _loading.value = false
    }

    fun check() {
        val bin = _query.value
        if (!BinInput.isValid(bin)) {
            _result.value = null
            _error.value = LookupError.INVALID
            return
        }
        lookupJob?.cancel()
        val id = ++requestId
        lookupJob = viewModelScope.launch {
            _error.value = null
            val now = System.currentTimeMillis()

            // Recent result on this device: answer instantly without a network call.
            val cached = historyStore.find(bin)
            if (cached != null && now - cached.timestamp < AppConfig.CACHE_MAX_AGE_MS) {
                val refreshed = cached.copy(timestamp = now)
                _result.value = refreshed
                withContext(Dispatchers.IO) { historyStore.upsert(refreshed) }
                return@launch
            }

            _loading.value = true
            try {
                val outcome = withContext(Dispatchers.IO) { lookup(bin) }
                when (outcome) {
                    is LookupResult.Success -> {
                        val info = outcome.info.copy(favorite = cached?.favorite ?: false)
                        _result.value = info
                        if (settingsStore.settings.value.saveHistory || info.favorite) {
                            withContext(Dispatchers.IO) { historyStore.upsert(info) }
                        }
                    }
                    else -> {
                        _result.value = null
                        _error.value = when (outcome) {
                            LookupResult.NotFound -> LookupError.NOT_FOUND
                            LookupResult.NoNetwork -> LookupError.NO_NETWORK
                            LookupResult.RateLimited -> LookupError.RATE_LIMIT
                            else -> LookupError.SERVER
                        }
                    }
                }
            } finally {
                // A newer request may already be running; only the latest one owns the spinner.
                if (id == requestId) _loading.value = false
            }
        }
    }

    /** Tries the digits as typed, then falls back to the 6 digit BIN for 7 and 8 digit input. */
    private fun lookup(bin: String): LookupResult {
        val first = BinApi.fetch(bin, BuildConfig.BIN_API_KEY)
        if (first is LookupResult.NotFound && bin.length > BinInput.MIN_LENGTH) {
            return BinApi.fetch(bin.take(BinInput.MIN_LENGTH), BuildConfig.BIN_API_KEY)
        }
        return first
    }

    /** Shows a stored entry on the home screen again. */
    fun open(info: BinInfo) {
        requestId++
        lookupJob?.cancel()
        _loading.value = false
        _query.value = info.bin
        _error.value = null
        _result.value = info
    }

    fun toggleFavorite(info: BinInfo) {
        val updated = info.copy(favorite = !info.favorite)
        if (_result.value?.bin == info.bin) _result.value = updated
        viewModelScope.launch(Dispatchers.IO) {
            if (historyStore.find(info.bin) == null) {
                historyStore.upsert(updated)
            } else {
                historyStore.setFavorite(info.bin, updated.favorite)
            }
        }
    }

    fun delete(info: BinInfo) {
        if (_result.value?.bin == info.bin) {
            _result.value = _result.value?.copy(favorite = false)
        }
        viewModelScope.launch(Dispatchers.IO) { historyStore.delete(info.bin) }
    }

    fun clearHistory() {
        _result.value = _result.value?.copy(favorite = false)
        viewModelScope.launch(Dispatchers.IO) { historyStore.clear() }
    }

    fun setThemeMode(mode: ThemeMode) = settingsStore.setThemeMode(mode)

    fun setSaveHistory(enabled: Boolean) = settingsStore.setSaveHistory(enabled)
}
