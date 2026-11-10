package com.kamildex.kbinchecker.data

import org.json.JSONObject

/** One BIN lookup result. Also the record stored in local history. */
data class BinInfo(
    val bin: String,
    val scheme: String,
    val type: String,
    val tier: String,
    val issuer: String,
    val countryName: String,
    val countryCode: String,
    val currency: String,
    val timestamp: Long,
    val favorite: Boolean = false
) {
    val cardScheme: CardScheme
        get() {
            val named = CardScheme.fromName(scheme)
            return if (named != CardScheme.UNKNOWN) named else CardScheme.detect(bin)
        }

    val schemeLabel: String
        get() = if (cardScheme != CardScheme.UNKNOWN) cardScheme.label else BinInput.pretty(scheme)

    val typeLabel: String get() = BinInput.pretty(type)
    val tierLabel: String get() = BinInput.pretty(tier)
    val issuerLabel: String get() = issuer.trim().ifEmpty { "Unknown issuer" }
    val countryLabel: String get() = countryName.trim().ifEmpty { "—" }

    /** "Mastercard · Credit" */
    val summary: String
        get() = if (type.isBlank()) schemeLabel else "$schemeLabel · $typeLabel"

    fun shareText(): String = buildString {
        append("BIN ").append(bin).append('\n')
        append("Network: ").append(schemeLabel).append('\n')
        append("Type: ").append(typeLabel).append('\n')
        append("Level: ").append(tierLabel).append('\n')
        append("Issuer: ").append(issuerLabel).append('\n')
        append("Country: ").append(countryLabel)
        if (countryCode.isNotBlank()) append(" (").append(countryCode).append(')')
        if (currency.isNotBlank()) append('\n').append("Currency: ").append(currency)
        append("\n\nChecked with KBin Checker")
    }

    fun toJson(): JSONObject = JSONObject()
        .put("bin", bin)
        .put("scheme", scheme)
        .put("type", type)
        .put("tier", tier)
        .put("issuer", issuer)
        .put("countryName", countryName)
        .put("countryCode", countryCode)
        .put("currency", currency)
        .put("timestamp", timestamp)
        .put("favorite", favorite)

    companion object {
        fun fromJson(o: JSONObject): BinInfo = BinInfo(
            bin = o.optString("bin"),
            scheme = o.optString("scheme"),
            type = o.optString("type"),
            tier = o.optString("tier"),
            issuer = o.optString("issuer"),
            countryName = o.optString("countryName"),
            countryCode = o.optString("countryCode"),
            currency = o.optString("currency"),
            timestamp = o.optLong("timestamp"),
            favorite = o.optBoolean("favorite")
        )
    }
}
