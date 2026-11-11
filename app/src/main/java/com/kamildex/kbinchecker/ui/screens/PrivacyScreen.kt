package com.kamildex.kbinchecker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kamildex.kbinchecker.AppConfig
import com.kamildex.kbinchecker.ui.components.AppCard
import com.kamildex.kbinchecker.ui.components.ScreenTopBar

/** In-app copy of the privacy policy. Keep it in sync with docs/privacy-policy.html. */
private val SECTIONS = listOf(
    "Summary" to
        "KBin Checker has no accounts, no ads and no analytics. It does not ask for, store or " +
        "transmit full card numbers, expiry dates, CVV codes or cardholder names.",
    "What the app sends" to
        "When you tap Check BIN, the 6 to 8 digits you entered are sent over an encrypted (HTTPS) " +
        "connection to our lookup service, which is hosted on Cloudflare and retrieves the public " +
        "issuer record from our BIN data provider, HandyAPI (handyapi.com). As with any internet " +
        "request, these services can see your IP address. The request contains no name, device " +
        "identifier or other personal information, and results are cached by BIN only, with no " +
        "link to who asked.",
    "What stays on your device" to
        "Your lookup history, favorites and settings are stored only in the app's private storage " +
        "on your device. They are never uploaded. You can switch history off or clear it in " +
        "Settings, and uninstalling the app removes everything.",
    "Permissions" to
        "The app uses only the Internet permission, which is required to perform lookups.",
    "Children" to
        "KBin Checker is a general reference tool and is not directed at children.",
    "Changes and contact" to
        "If this policy changes, the updated version will be published with the app. Questions " +
        "can be sent through " + AppConfig.WEBSITE_URL + "."
)

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTopBar(title = "Privacy policy", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            SECTIONS.forEach { (title, body) ->
                AppCard {
                    CardTitle(title)
                    Spacer(Modifier.height(8.dp))
                    BodyText(body)
                }
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
