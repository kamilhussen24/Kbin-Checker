package com.kamildex.kbinchecker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kamildex.kbinchecker.data.BinInfo
import com.kamildex.kbinchecker.ui.screens.AboutScreen
import com.kamildex.kbinchecker.ui.screens.DetailSheet
import com.kamildex.kbinchecker.ui.screens.HistoryScreen
import com.kamildex.kbinchecker.ui.screens.HomeScreen
import com.kamildex.kbinchecker.ui.screens.PrivacyScreen
import com.kamildex.kbinchecker.ui.screens.SettingsScreen

private enum class Tab(val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    HOME("Home", Icons.Outlined.Home, Icons.Rounded.Home),
    HISTORY("History", Icons.Outlined.History, Icons.Rounded.History),
    ABOUT("About", Icons.Outlined.Info, Icons.Rounded.Info)
}

private enum class Page { TABS, SETTINGS, PRIVACY }

@Composable
fun KBinApp(vm: MainViewModel) {
    var tab by rememberSaveable { mutableStateOf(Tab.HOME) }
    var page by rememberSaveable { mutableStateOf(Page.TABS) }
    var detail by remember { mutableStateOf<BinInfo?>(null) }

    BackHandler(enabled = page == Page.TABS && tab != Tab.HOME) { tab = Tab.HOME }
    BackHandler(enabled = page != Page.TABS) { page = Page.TABS }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        when (page) {
            Page.TABS -> Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (tab) {
                        Tab.HOME -> HomeScreen(
                            vm = vm,
                            onOpenSettings = { page = Page.SETTINGS },
                            onOpenDetail = { detail = it },
                            onSeeHistory = { tab = Tab.HISTORY }
                        )
                        Tab.HISTORY -> HistoryScreen(
                            vm = vm,
                            onOpenDetail = { detail = it }
                        )
                        Tab.ABOUT -> AboutScreen(
                            onOpenPrivacy = { page = Page.PRIVACY }
                        )
                    }
                }
                BottomBar(selected = tab, onSelect = { tab = it })
            }
            Page.SETTINGS -> SettingsScreen(vm = vm, onBack = { page = Page.TABS })
            Page.PRIVACY -> PrivacyScreen(onBack = { page = Page.TABS })
        }

        val shown = detail
        if (shown != null) {
            DetailSheet(
                info = shown,
                onDismiss = { detail = null },
                onToggleFavorite = {
                    vm.toggleFavorite(shown)
                    detail = shown.copy(favorite = !shown.favorite)
                },
                onDelete = {
                    vm.delete(shown)
                    detail = null
                },
                onCheckAgain = {
                    vm.open(shown)
                    detail = null
                    tab = Tab.HOME
                }
            )
        }
    }
}

@Composable
private fun BottomBar(selected: Tab, onSelect: (Tab) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth().background(colors.surface)) {
        HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(66.dp)
        ) {
            Tab.entries.forEach { item ->
                val isSelected = item == selected
                val tint = if (isSelected) colors.primary else colors.onSurfaceVariant
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(66.dp)
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = { onSelect(item) }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = tint
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(if (isSelected) colors.primary else Color.Transparent)
                    )
                }
            }
        }
    }
}
