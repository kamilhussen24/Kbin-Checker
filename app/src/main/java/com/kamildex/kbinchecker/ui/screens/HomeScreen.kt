package com.kamildex.kbinchecker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamildex.kbinchecker.data.BinInfo
import com.kamildex.kbinchecker.data.BinInput
import com.kamildex.kbinchecker.data.CardScheme
import com.kamildex.kbinchecker.ui.LookupError
import com.kamildex.kbinchecker.ui.MainViewModel
import com.kamildex.kbinchecker.ui.components.ActionChip
import com.kamildex.kbinchecker.ui.components.AppCard
import com.kamildex.kbinchecker.ui.components.BrandGradient
import com.kamildex.kbinchecker.ui.components.BrandGradientHorizontal
import com.kamildex.kbinchecker.ui.components.BrandMark
import com.kamildex.kbinchecker.ui.components.Chevron
import com.kamildex.kbinchecker.ui.components.CircleIconButton
import com.kamildex.kbinchecker.ui.components.HistoryRow
import com.kamildex.kbinchecker.ui.components.InfoCell
import com.kamildex.kbinchecker.ui.components.SchemeBadge
import com.kamildex.kbinchecker.ui.components.SectionHeader
import com.kamildex.kbinchecker.ui.components.copyText
import com.kamildex.kbinchecker.ui.components.readClipboard
import com.kamildex.kbinchecker.ui.components.shareText
import com.kamildex.kbinchecker.ui.components.timeAgo
import com.kamildex.kbinchecker.ui.theme.success
import com.kamildex.kbinchecker.ui.theme.successContainer

@Composable
fun HomeScreen(
    vm: MainViewModel,
    onOpenSettings: () -> Unit,
    onOpenDetail: (BinInfo) -> Unit,
    onSeeHistory: () -> Unit
) {
    val query by vm.query.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val result by vm.result.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val history by vm.history.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val colors = MaterialTheme.colorScheme

    var showBinInfo by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }

    val scheme = CardScheme.detect(query)
    val currentError = error
    val submit = {
        keyboard?.hide()
        vm.check()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BrandMark(size = 38.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = "KBin Checker",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                color = colors.onBackground,
                modifier = Modifier.weight(1f)
            )
            CircleIconButton(
                icon = Icons.Outlined.Settings,
                contentDescription = "Settings",
                onClick = onOpenSettings
            )
        }

        HeroCard(query = query, scheme = scheme)

        Text(
            text = buildAnnotatedString {
                append("Card BIN ")
                withStyle(SpanStyle(color = colors.primary)) { append("Lookup") }
            },
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.8).sp,
            textAlign = TextAlign.Center,
            color = colors.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Issuer, network, type and country in seconds",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = colors.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(22.dp))

        // Input
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Enter card BIN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "What is a BIN?",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button) { showBinInfo = true }
                        .padding(4.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            BinField(
                value = query,
                isError = currentError == LookupError.INVALID,
                onValueChange = vm::onQueryChange,
                onSubmit = submit,
                onPaste = { vm.onQueryChange(readClipboard(context)) },
                onClear = vm::clearQuery
            )
            if (currentError == LookupError.INVALID) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = LookupError.INVALID.title + ". " + LookupError.INVALID.message,
                    fontSize = 12.5.sp,
                    color = colors.error
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .graphicsLayer { alpha = if (loading) 0.75f else 1f }
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandGradientHorizontal)
                    .clickable(enabled = !loading, role = Role.Button, onClick = submit),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (loading) "Checking" else "Check BIN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        // Result
        AnimatedVisibility(
            visible = result != null,
            enter = fadeIn() + expandVertically(),
            exit = ExitTransition.None
        ) {
            result?.let { info ->
                Column {
                    Spacer(Modifier.height(14.dp))
                    ResultCard(
                        info = info,
                        onCopy = { copyText(context, "BIN details", info.shareText()) },
                        onShare = { shareText(context, info.shareText()) },
                        onFavorite = { vm.toggleFavorite(info) },
                        onDetails = { onOpenDetail(info) }
                    )
                }
            }
        }

        // Lookup problem
        if (currentError != null && currentError != LookupError.INVALID) {
            Spacer(Modifier.height(14.dp))
            ErrorCard(error = currentError, scheme = scheme, onRetry = submit)
        }

        // Recent
        Spacer(Modifier.height(24.dp))
        SectionHeader(
            title = "Recent checks",
            action = if (history.isNotEmpty()) "Clear all" else null,
            onAction = { confirmClear = true }
        )
        Spacer(Modifier.height(10.dp))
        if (history.isEmpty()) {
            Text(
                text = "Your recent lookups will appear here.",
                fontSize = 14.sp,
                color = colors.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                history.take(3).forEach { item ->
                    HistoryRow(info = item, onClick = { onOpenDetail(item) }) {
                        Text(
                            text = timeAgo(item.timestamp),
                            fontSize = 12.5.sp,
                            color = colors.onSurfaceVariant
                        )
                        Spacer(Modifier.width(2.dp))
                        Chevron()
                    }
                }
            }
            if (history.size > 3) {
                TextButton(
                    onClick = onSeeHistory,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("View all history", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Only the first 6 to 8 digits are needed. Never enter a full card number, expiry date or CVV.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center,
            color = colors.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
    }

    if (showBinInfo) {
        AlertDialog(
            onDismissRequest = { showBinInfo = false },
            confirmButton = {
                TextButton(onClick = { showBinInfo = false }) { Text("Got it") }
            },
            title = { Text("What is a BIN?") },
            text = {
                Text(
                    "The Bank Identification Number (BIN), also called IIN, is the first 6 to 8 digits " +
                        "of a payment card. It identifies the card network and the bank that issued the " +
                        "card. It does not identify a cardholder or an account."
                )
            }
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearHistory()
                    confirmClear = false
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancel") }
            },
            title = { Text("Clear history?") },
            text = { Text("All saved lookups and favorites on this device will be removed.") }
        )
    }
}

/** Card illustration that mirrors what the user types. */
@Composable
private fun HeroCard(query: String, scheme: CardScheme) {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier.fillMaxWidth().height(204.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Sized from the height so the halo stays below the header on every screen width.
            val center = Offset(size.width / 2f, size.height * 0.60f)
            drawCircle(color = accent.copy(alpha = 0.06f), radius = size.height * 0.58f, center = center)
            drawCircle(color = accent.copy(alpha = 0.07f), radius = size.height * 0.40f, center = center)
        }
        Box(
            modifier = Modifier
                .size(width = 252.dp, height = 156.dp)
                .graphicsLayer { rotationZ = -7f }
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = accent,
                    spotColor = accent
                )
                .clip(RoundedCornerShape(20.dp))
                .background(BrandGradient)
                .padding(18.dp)
        ) {
            // Chip
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(width = 36.dp, height = 27.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.9f))
            )
            // Contactless mark
            Canvas(modifier = Modifier.align(Alignment.TopEnd).size(24.dp)) {
                val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                val origin = Offset(size.width * 0.15f, size.height / 2f)
                for (i in 1..3) {
                    val r = size.width * 0.25f * i
                    drawArc(
                        color = Color.White.copy(alpha = 0.9f),
                        startAngle = -42f,
                        sweepAngle = 84f,
                        useCenter = false,
                        topLeft = Offset(origin.x - r, origin.y - r),
                        size = Size(r * 2f, r * 2f),
                        style = stroke
                    )
                }
            }
            Text(
                text = BinInput.preview(query),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.align(Alignment.CenterStart).padding(top = 26.dp)
            )
            Text(
                text = "KDex",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            if (scheme != CardScheme.UNKNOWN) {
                Text(
                    text = scheme.label.uppercase(),
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
private fun BinField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onPaste: () -> Unit,
    onClear: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val borderColor = when {
        isError -> colors.error
        focused -> colors.primary
        else -> colors.outlineVariant
    }
    val shape = RoundedCornerShape(16.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        ),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        interactionSource = interaction,
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(shape)
                    .background(colors.surfaceVariant)
                    .border(1.dp, borderColor, shape)
                    .padding(start = 16.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CreditCard,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = "First 6 to 8 digits",
                            fontSize = 16.sp,
                            color = colors.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }
                    innerTextField()
                }
                if (value.isEmpty()) {
                    TextButton(onClick = onPaste) {
                        Text("Paste", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button, onClick = onClear)
                            .padding(10.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun ResultCard(
    info: BinInfo,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit,
    onDetails: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(colors.successContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = colors.success,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BIN found",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.success
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = info.summary,
                    fontSize = 13.5.sp,
                    color = colors.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            SchemeBadge(info.cardScheme)
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            InfoCell("Bank", info.issuerLabel, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            InfoCell("Country", countryText(info), Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            InfoCell("Type", info.typeLabel, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            InfoCell("Level", info.tierLabel, Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionChip(Icons.Outlined.ContentCopy, "Copy", "Copy details", onCopy, Modifier.weight(1f))
            ActionChip(Icons.Outlined.Share, "Share", "Share details", onShare, Modifier.weight(1f))
            ActionChip(
                icon = if (info.favorite) Icons.Rounded.Star else Icons.Outlined.StarBorder,
                label = if (info.favorite) "Saved" else "Save",
                contentDescription = "Favorite",
                onClick = onFavorite,
                modifier = Modifier.weight(1f),
                active = info.favorite
            )
            ActionChip(
                icon = Icons.Outlined.ChevronRight,
                label = null,
                contentDescription = "Details",
                onClick = onDetails,
                modifier = Modifier.width(44.dp)
            )
        }
    }
}

@Composable
private fun ErrorCard(error: LookupError, scheme: CardScheme, onRetry: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    AppCard {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(colors.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (error == LookupError.NO_NETWORK) Icons.Outlined.WifiOff else Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = error.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = error.message,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    color = colors.onSurfaceVariant
                )
                if (scheme != CardScheme.UNKNOWN) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Network from prefix: ${scheme.label}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )
                }
            }
        }
        if (error != LookupError.NOT_FOUND) {
            Spacer(Modifier.height(6.dp))
            TextButton(onClick = onRetry, modifier = Modifier.align(Alignment.End)) {
                Text("Try again", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

internal fun countryText(info: BinInfo): String =
    if (info.countryCode.isBlank() || info.countryName.isBlank()) info.countryLabel
    else "${info.countryName} (${info.countryCode})"
