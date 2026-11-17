package com.kamildex.kbinchecker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamildex.kbinchecker.data.BinInfo
import com.kamildex.kbinchecker.ui.MainViewModel
import com.kamildex.kbinchecker.ui.components.HistoryRow

@Composable
fun HistoryScreen(vm: MainViewModel, onOpenDetail: (BinInfo) -> Unit) {
    val history by vm.history.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    var search by rememberSaveable { mutableStateOf("") }
    var favoritesOnly by rememberSaveable { mutableStateOf(false) }

    val needle = search.trim()
    val filtered = remember(history, needle, favoritesOnly) {
        history.filter { item ->
            (!favoritesOnly || item.favorite) &&
                (needle.isEmpty() ||
                    item.bin.contains(needle) ||
                    item.issuer.contains(needle, ignoreCase = true) ||
                    item.countryName.contains(needle, ignoreCase = true) ||
                    item.schemeLabel.contains(needle, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "History",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            color = colors.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (history.isEmpty()) "Saved on this device only" else "${history.size} saved on this device",
            fontSize = 14.sp,
            color = colors.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        SearchField(value = search, onValueChange = { search = it })

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterPill(label = "All", selected = !favoritesOnly, onClick = { favoritesOnly = false })
            FilterPill(label = "Favorites", selected = favoritesOnly, onClick = { favoritesOnly = true })
        }
        Spacer(Modifier.height(14.dp))

        if (filtered.isEmpty()) {
            EmptyState(
                title = when {
                    history.isEmpty() -> "No lookups yet"
                    favoritesOnly && needle.isEmpty() -> "No favorites yet"
                    else -> "No matches"
                },
                message = when {
                    history.isEmpty() -> "BINs you check will be listed here."
                    favoritesOnly && needle.isEmpty() -> "Tap the star on a result to keep it here."
                    else -> "Try a different BIN, bank or country."
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(filtered, key = { it.bin }) { item ->
                    HistoryRow(info = item, onClick = { onOpenDetail(item) }) {
                        Icon(
                            imageVector = if (item.favorite) Icons.Rounded.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (item.favorite) "Remove favorite" else "Add favorite",
                            tint = if (item.favorite) colors.primary else colors.onSurfaceVariant,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(role = Role.Button) { vm.toggleFavorite(item) }
                                .padding(9.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.take(40)) },
        singleLine = true,
        textStyle = TextStyle(color = colors.onSurface, fontSize = 15.sp),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shape)
                    .background(colors.surface)
                    .border(1.dp, colors.outlineVariant, shape)
                    .padding(start = 14.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Search BIN, bank or country",
                            fontSize = 15.sp,
                            color = colors.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }
                    innerTextField()
                }
                if (value.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear search",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button) { onValueChange("") }
                            .padding(10.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(shape)
            .background(if (selected) colors.primary else colors.surface)
            .border(1.dp, if (selected) colors.primary else colors.outlineVariant, shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) colors.onPrimary else colors.onSurface
        )
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = colors.onSurfaceVariant
        )
    }
}
