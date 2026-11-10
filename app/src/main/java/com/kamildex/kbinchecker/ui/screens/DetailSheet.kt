package com.kamildex.kbinchecker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kamildex.kbinchecker.data.BinInfo
import com.kamildex.kbinchecker.ui.components.ActionChip
import com.kamildex.kbinchecker.ui.components.SchemeBadge
import com.kamildex.kbinchecker.ui.components.copyText
import com.kamildex.kbinchecker.ui.components.fullDate
import com.kamildex.kbinchecker.ui.components.shareText

/** Full details for one BIN, opened from a result or a history row. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSheet(
    info: BinInfo,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCheckAgain: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = info.bin,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = info.summary,
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                SchemeBadge(info.cardScheme)
            }

            Spacer(Modifier.height(18.dp))
            DetailLine("Bank", info.issuerLabel)
            DetailLine("Network", info.schemeLabel)
            DetailLine("Type", info.typeLabel)
            DetailLine("Level", info.tierLabel)
            DetailLine("Country", countryText(info))
            DetailLine("Currency", info.currency.ifBlank { "—" })
            DetailLine("Checked", fullDate(info.timestamp), last = true)

            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionChip(
                    icon = Icons.Outlined.ContentCopy,
                    label = "Copy",
                    contentDescription = "Copy details",
                    onClick = { copyText(context, "BIN details", info.shareText()) },
                    modifier = Modifier.weight(1f)
                )
                ActionChip(
                    icon = Icons.Outlined.Share,
                    label = "Share",
                    contentDescription = "Share details",
                    onClick = { shareText(context, info.shareText()) },
                    modifier = Modifier.weight(1f)
                )
                ActionChip(
                    icon = if (info.favorite) Icons.Rounded.Star else Icons.Outlined.StarBorder,
                    label = if (info.favorite) "Saved" else "Save",
                    contentDescription = "Favorite",
                    onClick = onToggleFavorite,
                    modifier = Modifier.weight(1f),
                    active = info.favorite
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionChip(
                    icon = Icons.Rounded.Search,
                    label = "Open on Home",
                    contentDescription = "Open on Home",
                    onClick = onCheckAgain,
                    modifier = Modifier.weight(1f)
                )
                ActionChip(
                    icon = Icons.Outlined.Delete,
                    label = "Remove",
                    contentDescription = "Remove from history",
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String, last: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = colors.onSurfaceVariant,
            modifier = Modifier.width(96.dp)
        )
        Text(
            text = value,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
    if (!last) HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
}
