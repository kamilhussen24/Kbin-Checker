package com.kamildex.kbinchecker

/** Values you may want to change before publishing. */
object AppConfig {
    /** Public website shown on the About screen. */
    const val WEBSITE_URL = "https://kamildex.com"

    /** Support address shown on the About screen. Leave empty to hide the row. */
    const val CONTACT_EMAIL = "contact@kamildex.com"

    /** Lookup endpoint, set at build time (BIN_API_URL secret). The BIN is appended to it. */
    val BIN_API_URL: String = BuildConfig.BIN_API_URL.trimEnd('/') + "/"

    /** Cached results older than this are fetched again. */
    const val CACHE_MAX_AGE_MS = 30L * 24 * 60 * 60 * 1000

    /** Maximum number of history entries kept on the device. */
    const val HISTORY_LIMIT = 200
}
