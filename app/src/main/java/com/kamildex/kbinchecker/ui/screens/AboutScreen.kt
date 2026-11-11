package com.kamildex.kbinchecker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kamildex.kbinchecker.AppConfig
import com.kamildex.kbinchecker.BuildConfig
import com.kamildex.kbinchecker.ui.components.AppCard
import com.kamildex.kbinchecker.ui.components.BrandMark
import com.kamildex.kbinchecker.ui.components.Chevron
import com.kamildex.kbinchecker.ui.components.openUrl
import com.kamildex.kbinchecker.ui.components.shareText

private val STORE_ID = BuildConfig.APPLICATION_ID.removeSuffix(".debug")
private val STORE_URL = "https://play.google.com/store/apps/details?id=$STORE_ID"

@Composable
fun AboutScreen(onOpenPrivacy: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandMark(size = 72.dp)
            Spacer(Modifier.height(14.dp))
            Text(
                text = "KBin Checker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp,
                color = colors.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Version ${BuildConfig.VERSION_NAME}",
                fontSize = 14.sp,
                color = colors.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(24.dp))
        AppCard {
            CardTitle("What is a BIN?")
            Spacer(Modifier.height(8.dp))
            BodyText(
                "The Bank Identification Number (BIN or IIN) is the first 6 to 8 digits of a payment " +
                    "card. It tells you which network the card runs on and which bank issued it."
            )
        }

        Spacer(Modifier.height(12.dp))
        AppCard {
            CardTitle("How it works")
            Spacer(Modifier.height(12.dp))
            Step(1, "Enter the first 6 to 8 digits of a card.")
            Spacer(Modifier.height(10.dp))
            Step(2, "KBin Checker looks the BIN up over a secure connection.")
            Spacer(Modifier.height(10.dp))
            Step(3, "See the bank, network, card type, level and country.")
        }

        Spacer(Modifier.height(12.dp))
        AppCard(padding = 6.dp) {
            LinkRow(Icons.Outlined.Shield, "Privacy policy", onClick = onOpenPrivacy)
            RowDivider()
            LinkRow(Icons.Outlined.StarBorder, "Rate on Google Play") {
                if (!openUrl(context, "market://details?id=$STORE_ID") && !openUrl(context, STORE_URL)) {
                    Toast.makeText(context, "Google Play is not available", Toast.LENGTH_SHORT).show()
                }
            }
            RowDivider()
            LinkRow(Icons.Outlined.Share, "Share this app") {
                shareText(context, "KBin Checker: look up any card BIN in seconds.\n$STORE_URL")
            }
            if (AppConfig.WEBSITE_URL.isNotBlank()) {
                RowDivider()
                LinkRow(Icons.Outlined.Language, "Website") {
                    openUrl(context, AppConfig.WEBSITE_URL)
                }
            }
            if (AppConfig.CONTACT_EMAIL.isNotBlank()) {
                RowDivider()
                LinkRow(Icons.Outlined.Email, "Contact support") {
                    if (!openUrl(context, "mailto:${AppConfig.CONTACT_EMAIL}")) {
                        Toast.makeText(context, AppConfig.CONTACT_EMAIL, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "KBin Checker is an informational tool. It is not affiliated with, endorsed by or " +
                "sponsored by any card network or bank. Results come from a third party database " +
                "and may be incomplete or out of date.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center,
            color = colors.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
internal fun CardTitle(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
internal fun BodyText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun Step(number: Int, text: String) {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(26.dp).clip(CircleShape).background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onPrimaryContainer
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@Composable
private fun LinkRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        Chevron()
    }
}
