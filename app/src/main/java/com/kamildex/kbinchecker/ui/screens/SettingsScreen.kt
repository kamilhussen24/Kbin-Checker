package com.kamildex.kbinchecker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamildex.kbinchecker.data.ThemeMode
import com.kamildex.kbinchecker.ui.MainViewModel
import com.kamildex.kbinchecker.ui.components.AppCard
import com.kamildex.kbinchecker.ui.components.ScreenTopBar

@Composable
fun SettingsScreen(vm: MainViewModel, onBack: () -> Unit) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val history by vm.history.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    var confirmClear by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTopBar(title = "Settings", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            AppCard {
                CardTitle("Appearance")
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surfaceVariant)
                        .padding(4.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val selected = settings.themeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) colors.primary else colors.surfaceVariant)
                                .clickable(role = Role.RadioButton) { vm.setThemeMode(mode) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) colors.onPrimary else colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            AppCard {
                CardTitle("History")
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Save lookups",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Keep results on this device for quick access and offline viewing.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Switch(
                        checked = settings.saveHistory,
                        onCheckedChange = { vm.setSaveHistory(it) }
                    )
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = { confirmClear = true },
                    enabled = history.isNotEmpty()
                ) {
                    Text(
                        text = if (history.isEmpty()) "History is empty" else "Clear history (${history.size})",
                        fontWeight = FontWeight.SemiBold,
                        color = if (history.isEmpty()) colors.onSurfaceVariant else colors.error
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            AppCard {
                CardTitle("Data source")
                Spacer(Modifier.height(8.dp))
                BodyText(
                    "BIN records are provided by HandyAPI. Only the digits you enter are sent, over an " +
                        "encrypted connection. Results are informational and may be incomplete."
                )
            }
            Spacer(Modifier.height(24.dp))
        }
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
