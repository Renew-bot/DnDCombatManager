package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatmanager.combat.i18n.Language
import com.example.dndcombatmanager.combat.i18n.LocalLanguage
import com.example.dndcombatmanager.combat.i18n.strings
import com.example.dndcombatmanager.combat.model.CharacterType
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import com.example.dndcombatmanager.combat.state.Layout
import com.example.dndcombatmanager.combat.state.label
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

@Composable
fun CombatTrackerScreen() {
    val state = remember { CombatTrackerState() }

    LaunchedEffect(Unit) { state.checkForUpdate() }

    CompositionLocalProvider(LocalLanguage provides state.language) {
        CombatTrackerContent(state)
    }
}

@Composable
private fun CombatTrackerContent(state: CombatTrackerState) {
    val s = strings()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(oklch(0.14f, 0.018f, 60f))
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val isNarrow = maxWidth < 880.dp
        val isVeryNarrow = maxWidth < 480.dp
        val stackAttacks = maxWidth < 1300.dp
        val isHeaderCompact = maxWidth < 1300.dp
        val headerMaxHeight = maxHeight * 0.2f

        // One-time default: phone-width screens open on Focus (one combatant at a time) instead of
        // Sidebar (list + sheet side by side), which needs more room to be readable. Only applies at
        // startup — doesn't fight the user if they pick a different layout or resize afterward.
        LaunchedEffect(Unit) {
            if (isNarrow) state.changeLayout(Layout.FOCUS)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            state.updateInfo?.let { UpdateBanner(it, state) }
            Header(state, maxHeight = headerMaxHeight, isCompact = isHeaderCompact, isVeryNarrow = isVeryNarrow)

            Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                when {
                    !state.hasCharacters -> EmptyState(state)
                    state.layout == Layout.SIDEBAR -> SidebarLayout(state, isNarrow, stackAttacks, modifier = Modifier.fillMaxWidth().padding(24.dp))
                    state.layout == Layout.TIMELINE -> TimelineLayout(state, isNarrow, stackAttacks, modifier = Modifier.fillMaxWidth().padding(24.dp))
                    state.layout == Layout.FOCUS -> FocusLayout(state, isNarrow, stackAttacks, modifier = Modifier.fillMaxWidth().padding(24.dp))
                }
            }
        }

        if (state.layout == Layout.FOCUS) {
            state.modalCharacter?.let { modalChar ->
                FocusModal(modalChar, isNarrow, stackAttacks, state)
            }
        }
    }

    CharacterFormDialog(state)
    PresetsDialog(state)
    CombatPresetsDialog(state)
    SaveCombatDialog(state)
    ProneDistanceDialog(state)

    state.pendingDeleteId?.let {
        ConfirmDialog(
            message = s.confirmRemoveCharacterMsg, confirmLabel = s.removeLabel,
            onConfirm = { state.confirmDelete() }, onDismiss = { state.cancelDelete() },
        )
    }
    if (state.pendingClearAll) {
        ConfirmDialog(
            message = s.confirmClearAllMsg, confirmLabel = s.clearBtn,
            onConfirm = { state.confirmClearAll() }, onDismiss = { state.cancelClearAll() },
        )
    }
    state.pendingDeletePresetId?.let {
        ConfirmDialog(
            message = s.confirmDeletePresetMsg, confirmLabel = s.deleteLabel,
            onConfirm = { state.confirmDeletePreset() }, onDismiss = { state.cancelDeletePreset() },
        )
    }
    state.pendingDeleteCombatPresetId?.let {
        ConfirmDialog(
            message = s.confirmDeleteCombatPresetMsg, confirmLabel = s.deleteLabel,
            onConfirm = { state.confirmDeleteCombatPreset() }, onDismiss = { state.cancelDeleteCombatPreset() },
        )
    }
    state.pendingLoadCombatPresetId?.let {
        ConfirmDialog(
            message = s.confirmLoadCombatPresetMsg, confirmLabel = s.loadLabel,
            onConfirm = { state.confirmLoadCombatPreset() }, onDismiss = { state.cancelLoadCombatPreset() },
        )
    }
}

private val CombatTrackerState.hasCharacters: Boolean get() = sortedCharacters.isNotEmpty()

/** Dismissible bar offering to open the latest GitHub release when a newer version is available. */
@Composable
private fun UpdateBanner(info: com.example.dndcombatmanager.combat.update.UpdateInfo, state: CombatTrackerState) {
    val s = strings()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(oklch(0.30f, 0.07f, 70f, 0.35f))
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            s.updateAvailableMsg(info.version), color = oklch(0.88f, 0.1f, 70f),
            fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
            modifier = Modifier.weight(1f),
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GradientPillButton(
                text = s.updateDownloadBtn,
                onClick = { com.example.dndcombatmanager.combat.platform.openUrl(info.url) },
                fontSize = 12.sp,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(7.dp),
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clickable { state.dismissUpdateBanner() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = oklch(0.80f, 0.1f, 70f), modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun Header(state: CombatTrackerState, maxHeight: Dp, isCompact: Boolean, isVeryNarrow: Boolean) {
    val s = strings()
    val showJump = state.viewingId != null && state.viewingId != state.activeId &&
        (state.layout == Layout.SIDEBAR || state.layout == Layout.TIMELINE)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .background(oklch(0.18f, 0.02f, 55f, 0.96f))
            .verticalScroll(rememberScrollState()),
    ) {
        if (isCompact) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    Text(
                        s.appTitle, color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false),
                    )
                    Box(
                        modifier = Modifier
                            .background(oklch(0.30f, 0.07f, 70f, 0.35f), RoundedCornerShape(999.dp))
                            .border(BorderStroke(1.dp, oklch(0.55f, 0.1f, 70f, 0.6f)), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            if (isVeryNarrow) s.roundShort(state.round) else s.round(state.round),
                            color = oklch(0.85f, 0.1f, 70f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
                        )
                    }
                }
                // FR/EN lives in the burger menu here, not inline — on phone-width screens there's no
                // room left for the switch next to the title/round pill without pushing the menu button
                // off-screen (it was happening even at max phone width, with the "N" of "EN" clipped).
                BurgerMenuButton(state = state, showJump = showJump)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 0.dp).padding(bottom = 12.dp)) {
                GradientPillButton(text = s.nextTurn, onClick = { state.nextTurn() }, fontSize = 13.5.sp, modifier = Modifier.fillMaxWidth(), trailingIcon = Icons.AutoMirrored.Filled.ArrowForward)
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(s.appTitle, color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Box(
                        modifier = Modifier
                            .background(oklch(0.30f, 0.07f, 70f, 0.35f), RoundedCornerShape(999.dp))
                            .border(BorderStroke(1.dp, oklch(0.55f, 0.1f, 70f, 0.6f)), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(s.round(state.round), color = oklch(0.85f, 0.1f, 70f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                    }
                    state.activeCharacter?.let { active ->
                        Row(modifier = Modifier.wrapContentWidth()) {
                            Text(s.activePrefix, color = oklch(0.65f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp, maxLines = 1)
                            Text(
                                active.name,
                                color = if (active.type == CharacterType.PJ) oklch(0.90f, 0.1f, 70f) else oklch(0.85f, 0.1f, 70f),
                                fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    if (showJump) {
                        PillButton(
                            text = s.jumpToActiveTurn, onClick = { state.jumpToActive() },
                            textColor = oklch(0.80f, 0.1f, 70f), background = androidx.compose.ui.graphics.Color.Transparent,
                            borderColor = oklch(0.50f, 0.09f, 70f, 0.7f), fontSize = 12.sp, shape = RoundedCornerShape(999.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 11.dp, vertical = 4.dp),
                            leadingIcon = Icons.Default.Refresh,
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LanguageSwitch(state)
                    Row(
                        modifier = Modifier
                            .background(oklch(0.21f, 0.02f, 55f), RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(10.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Layout.entries.forEach { l ->
                            val active = state.layout == l
                            Box(
                                modifier = Modifier
                                    .background(if (active) oklch(0.55f, 0.12f, 70f) else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(7.dp))
                                    .clickable { state.changeLayout(l) }
                                    .padding(horizontal = 13.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    l.label(state.language), color = if (active) oklch(0.15f, 0.02f, 60f) else oklch(0.65f, 0.02f, 70f),
                                    fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
                                )
                            }
                        }
                    }
                    PillButton(
                        text = s.characterPresetsBtn, onClick = { state.openPresets() },
                        textColor = oklch(0.85f, 0.02f, 80f), background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                    )
                    PillButton(
                        text = s.combatPresetsBtn, onClick = { state.openCombatPresets() },
                        textColor = oklch(0.85f, 0.02f, 80f), background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                    )
                    PillButton(
                        text = s.saveCombatBtn, onClick = { state.openSaveCombatDialog() },
                        textColor = if (state.hasCharacters) oklch(0.85f, 0.02f, 80f) else oklch(0.45f, 0.02f, 70f),
                        background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                        enabled = state.hasCharacters,
                    )
                    PillButton(
                        text = s.addCharacterBtn, onClick = { state.openAddForm() },
                        textColor = oklch(0.85f, 0.02f, 80f), background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                    )
                    GradientPillButton(text = s.nextTurn, onClick = { state.nextTurn() }, fontSize = 13.5.sp, trailingIcon = Icons.AutoMirrored.Filled.ArrowForward)
                    PillButton(
                        text = s.clearBtn, onClick = { state.requestClearAll() },
                        textColor = oklch(0.55f, 0.1f, 25f), background = androidx.compose.ui.graphics.Color.Transparent,
                        borderColor = oklch(0.40f, 0.08f, 25f, 0.6f), fontSize = 12.5.sp,
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(oklch(0.28f, 0.02f, 55f)))
    }
}

/** Compact FR/EN toggle for the top bar, persisted via [CombatTrackerState.changeLanguage]. */
@Composable
private fun LanguageSwitch(state: CombatTrackerState, modifier: Modifier = Modifier) {
    val s = strings()
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
        Text(
            s.languageToggleFr,
            color = if (state.language == Language.FR) oklch(0.85f, 0.1f, 70f) else oklch(0.50f, 0.02f, 70f),
            fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 11.sp,
        )
        Switch(
            checked = state.language == Language.EN,
            onCheckedChange = { checked -> state.changeLanguage(if (checked) Language.EN else Language.FR) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = oklch(0.90f, 0.05f, 70f),
                checkedTrackColor = oklch(0.55f, 0.12f, 70f),
                checkedBorderColor = oklch(0.70f, 0.13f, 70f),
                uncheckedThumbColor = oklch(0.80f, 0.02f, 70f),
                uncheckedTrackColor = oklch(0.24f, 0.02f, 55f),
                uncheckedBorderColor = oklch(0.36f, 0.02f, 55f),
            ),
            modifier = Modifier.scale(0.7f),
        )
        Text(
            s.languageToggleEn,
            color = if (state.language == Language.EN) oklch(0.85f, 0.1f, 70f) else oklch(0.50f, 0.02f, 70f),
            fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 11.sp,
        )
    }
}

@Composable
private fun BurgerMenuButton(state: CombatTrackerState, showJump: Boolean) {
    val s = strings()
    var expanded by remember { mutableStateOf(false) }
    Box {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(oklch(0.21f, 0.02f, 55f), RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(8.dp))
                .clickable { expanded = true },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Menu, contentDescription = null, tint = oklch(0.85f, 0.02f, 80f), modifier = Modifier.size(18.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            state.activeCharacter?.let { active ->
                DropdownMenuItem(text = { Text("${s.activePrefix}${active.name}", fontWeight = FontWeight.SemiBold) }, onClick = {}, enabled = false)
            }
            if (showJump) {
                DropdownMenuItem(
                    text = { Text(s.jumpToActiveTurn) },
                    leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    onClick = { state.jumpToActive(); expanded = false },
                )
            }
            HorizontalDivider()
            Layout.entries.forEach { l ->
                val active = state.layout == l
                DropdownMenuItem(
                    text = { Text(l.label(state.language)) },
                    leadingIcon = if (active) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null,
                    onClick = { state.changeLayout(l); expanded = false },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(s.languageToggleFr) },
                leadingIcon = if (state.language == Language.FR) { { Icon(Icons.Default.Check, contentDescription = null) } } else null,
                onClick = { state.changeLanguage(Language.FR); expanded = false },
            )
            DropdownMenuItem(
                text = { Text(s.languageToggleEn) },
                leadingIcon = if (state.language == Language.EN) { { Icon(Icons.Default.Check, contentDescription = null) } } else null,
                onClick = { state.changeLanguage(Language.EN); expanded = false },
            )
            HorizontalDivider()
            DropdownMenuItem(text = { Text(s.characterPresetsBtn) }, onClick = { state.openPresets(); expanded = false })
            DropdownMenuItem(text = { Text(s.combatPresetsBtn) }, onClick = { state.openCombatPresets(); expanded = false })
            DropdownMenuItem(
                text = { Text(s.saveCombatBtn) }, enabled = state.hasCharacters,
                onClick = { state.openSaveCombatDialog(); expanded = false },
            )
            DropdownMenuItem(text = { Text(s.addCharacterBtn) }, onClick = { state.openAddForm(); expanded = false })
            DropdownMenuItem(text = { Text(s.clearBtn) }, onClick = { state.requestClearAll(); expanded = false })
        }
    }
}

@Composable
private fun EmptyState(state: CombatTrackerState) {
    val s = strings()
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 90.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            s.emptyStateTitle, color = oklch(0.70f, 0.03f, 75f), fontFamily = Fonts.display, fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Text(
            s.emptyStateSubtitle, color = oklch(0.55f, 0.02f, 70f),
            fontFamily = Fonts.body, fontSize = 14.sp, modifier = Modifier.padding(bottom = 18.dp),
        )
        PillButton(
            text = s.emptyStateCta, onClick = { state.openAddForm() },
            textColor = oklch(0.85f, 0.02f, 80f), background = oklch(0.24f, 0.02f, 55f), borderColor = oklch(0.36f, 0.02f, 55f),
        )
    }
}

@Composable
private fun FocusModal(
    modalChar: com.example.dndcombatmanager.combat.model.Character,
    isNarrow: Boolean,
    stackAttacks: Boolean,
    state: CombatTrackerState,
) {
    Dialog(onDismissRequest = { state.closeModal() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(oklch(0.10f, 0.01f, 60f, 0.75f))
                .clickable { state.closeModal() }
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(vertical = 40.dp, horizontal = 20.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 820.dp)
                    .fillMaxWidth()
                    .consumeClicks(),
            ) {
                CharacterAndAttacksPane(
                    character = modalChar,
                    isActive = false,
                    state = state,
                    stacked = isNarrow,
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 14.dp, y = (-14).dp)
                        .size(32.dp)
                        .background(oklch(0.24f, 0.02f, 55f), RoundedCornerShape(999.dp))
                        .border(BorderStroke(1.dp, oklch(0.40f, 0.02f, 55f)), RoundedCornerShape(999.dp))
                        .clickable { state.closeModal() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = oklch(0.85f, 0.02f, 80f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
