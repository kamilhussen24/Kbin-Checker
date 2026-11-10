package com.kamildex.kbinchecker.data

import com.kamildex.kbinchecker.AppConfig
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Currency
import java.util.Locale

sealed class LookupResult {
    data class Success(val info: BinInfo) : LookupResult()
    data object NotFound : LookupResult()
    data object NoNetwork : LookupResult()
    data object RateLimited : LookupResult()
    data object Failed : LookupResult()
}

/**
 * Minimal HTTPS client for the BIN data provider (HandyAPI).
 * Uses the platform HttpURLConnection so the app ships with no networking library.
 * Only the 6 to 8 digit BIN is ever sent.
 */
object BinApi {

    /** Blocking call. Run it on an IO dispatcher. */
    fun fetch(bin: String, apiKey: String): LookupResult {
        var connection: HttpURLConnection? = null
        return try {
            val conn = URL(AppConfig.BIN_API_URL + bin).openConnection() as HttpURLConnection
            connection = conn
            conn.requestMethod = "GET"
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.setRequestProperty("Accept", "application/json")
            if (apiKey.isNotBlank()) conn.setRequestProperty("x-api-key", apiKey)
            val code = conn.responseCode
            when {
                code == 429 -> LookupResult.RateLimited
                code == 404 -> LookupResult.NotFound
                code !in 200..299 -> LookupResult.Failed
                else -> parse(bin, conn.inputStream.bufferedReader().use { it.readText() })
            }
        } catch (e: IOException) {
            LookupResult.NoNetwork
        } catch (e: JSONException) {
            LookupResult.Failed
        } catch (e: RuntimeException) {
            LookupResult.Failed
        } finally {
            connection?.disconnect()
        }
    }

    internal fun parse(bin: String, body: String): LookupResult {
        val json = JSONObject(body)
        val status = text(json, "Status").uppercase()
        if (status != "SUCCESS") {
            return if ("LIMIT" in status) LookupResult.RateLimited else LookupResult.NotFound
        }
        val scheme = text(json, "Scheme")
        val issuer = text(json, "Issuer")
        if (scheme.isEmpty() && issuer.isEmpty()) return LookupResult.NotFound

        val country = json.optJSONObject("Country")
        val countryCode = if (country != null) text(country, "A2").uppercase() else ""
        val countryName = if (country != null) text(country, "Name") else ""

        return LookupResult.Success(
            BinInfo(
                bin = bin,
                scheme = scheme,
                type = text(json, "Type"),
                tier = text(json, "CardTier"),
                issuer = issuer,
                countryName = countryName,
                countryCode = countryCode,
                currency = currencyFor(countryCode),
                timestamp = System.currentTimeMillis()
            )
        )
    }

    private fun text(o: JSONObject, key: String): String =
        if (o.isNull(key)) "" else o.optString(key).trim()

    /** ISO currency code for a two letter country code, or empty when unknown. */
    fun currencyFor(countryCode: String): String {
        if (countryCode.length != 2) return ""
        return try {
            Currency.getInstance(Locale("", countryCode))?.currencyCode.orEmpty()
        } catch (e: Exception) {
            ""
        }
    }
}
