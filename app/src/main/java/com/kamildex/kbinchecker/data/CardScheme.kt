package com.kamildex.kbinchecker.data

/** Card networks, detected locally from the public IIN prefix ranges. */
enum class CardScheme(val label: String, val short: String) {
    VISA("Visa", "VISA"),
    MASTERCARD("Mastercard", "MC"),
    AMEX("American Express", "AMEX"),
    DISCOVER("Discover", "DISC"),
    JCB("JCB", "JCB"),
    DINERS("Diners Club", "DC"),
    UNIONPAY("UnionPay", "UP"),
    MAESTRO("Maestro", "MAES"),
    RUPAY("RuPay", "RUPAY"),
    MIR("Mir", "MIR"),
    UNKNOWN("Card", "CARD");

    companion object {
        private val MAESTRO_4 = setOf(5018, 5020, 5038, 5893, 6304, 6759, 6761, 6762, 6763)

        /** Best-effort network detection from the leading digits. Works offline. */
        fun detect(input: String): CardScheme {
            val d = input.filter { it in '0'..'9' }
            if (d.isEmpty()) return UNKNOWN
            fun prefix(n: Int): Int = if (d.length >= n) d.substring(0, n).toInt() else -1
            val p1 = prefix(1)
            val p2 = prefix(2)
            val p3 = prefix(3)
            val p4 = prefix(4)
            return when {
                p1 == 4 -> VISA
                p2 == 34 || p2 == 37 -> AMEX
                p4 in 2200..2204 -> MIR
                p2 in 51..55 || p4 in 2221..2720 -> MASTERCARD
                p4 in 3528..3589 -> JCB
                p3 in 300..305 || p2 == 36 || p2 == 38 || p2 == 39 -> DINERS
                p4 == 6011 -> DISCOVER
                p4 == 6521 || p4 == 6522 || p3 == 508 || (p2 == 60 && p4 != -1) -> RUPAY
                p2 == 65 || p3 in 644..649 -> DISCOVER
                p2 == 62 || p2 == 81 -> UNIONPAY
                p4 in MAESTRO_4 || p2 == 50 || p2 in 56..58 || p2 == 63 || p2 == 67 -> MAESTRO
                else -> UNKNOWN
            }
        }

        /** Maps a provider scheme name (for example "MASTERCARD") to a network. */
        fun fromName(name: String?): CardScheme {
            val n = name.orEmpty().uppercase()
            return when {
                n.isBlank() -> UNKNOWN
                "VISA" in n -> VISA
                "MASTER" in n -> MASTERCARD
                "AMEX" in n || "AMERICAN" in n -> AMEX
                "DISCOVER" in n -> DISCOVER
                "JCB" in n -> JCB
                "DINERS" in n -> DINERS
                "UNION" in n -> UNIONPAY
                "MAESTRO" in n -> MAESTRO
                "RUPAY" in n -> RUPAY
                n == "MIR" -> MIR
                else -> UNKNOWN
            }
        }
    }
}

/** Input rules shared by the UI and the view model. */
object BinInput {
    const val MIN_LENGTH = 6
    const val MAX_LENGTH = 8

    /** Keeps digits only and never more than [MAX_LENGTH], so a full card number is never held. */
    fun sanitize(raw: String): String = raw.filter { it in '0'..'9' }.take(MAX_LENGTH)

    fun isValid(bin: String): Boolean =
        bin.length in MIN_LENGTH..MAX_LENGTH && bin.all { it in '0'..'9' }

    /** "516706" becomes "5167 06•• •••• ••••" for the card preview. */
    fun preview(bin: String): String {
        val padded = bin.padEnd(16, '•')
        return padded.chunked(4).joinToString(" ")
    }

    /** "CREDIT CARD" becomes "Credit Card". Blank input becomes a dash. */
    fun pretty(value: String): String {
        val v = value.trim()
        if (v.isEmpty()) return "—"
        return v.lowercase().split(' ').filter { it.isNotEmpty() }
            .joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } }
    }
}
