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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatmanager.combat.model.CharacterType
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import com.example.dndcombatmanager.combat.state.Layout
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

@Composable
fun CombatTrackerScreen() {
    val state = remember { CombatTrackerState() }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(oklch(0.14f, 0.018f, 60f))
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val isNarrow = maxWidth < 880.dp
        val stackAttacks = maxWidth < 1300.dp
        val isHeaderCompact = maxWidth < 1300.dp
        val headerMaxHeight = maxHeight * 0.2f

        Column(modifier = Modifier.fillMaxSize()) {
            Header(state, maxHeight = headerMaxHeight, isCompact = isHeaderCompact)

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

    state.pendingDeleteId?.let {
        ConfirmDialog(
            message = "Retirer ce personnage du combat ?", confirmLabel = "Retirer",
            onConfirm = { state.confirmDelete() }, onDismiss = { state.cancelDelete() },
        )
    }
    if (state.pendingClearAll) {
        ConfirmDialog(
            message = "Vider tout le combat ? Cette action est irréversible.", confirmLabel = "Vider",
            onConfirm = { state.confirmClearAll() }, onDismiss = { state.cancelClearAll() },
        )
    }
    state.pendingDeletePresetId?.let {
        ConfirmDialog(
            message = "Supprimer ce préset ?", confirmLabel = "Supprimer",
            onConfirm = { state.confirmDeletePreset() }, onDismiss = { state.cancelDeletePreset() },
        )
    }
    state.pendingDeleteCombatPresetId?.let {
        ConfirmDialog(
            message = "Supprimer ce preset de combat ?", confirmLabel = "Supprimer",
            onConfirm = { state.confirmDeleteCombatPreset() }, onDismiss = { state.cancelDeleteCombatPreset() },
        )
    }
    state.pendingLoadCombatPresetId?.let {
        ConfirmDialog(
            message = "Charger ce preset de combat ? Le combat actuel sera remplacé.", confirmLabel = "Charger",
            onConfirm = { state.confirmLoadCombatPreset() }, onDismiss = { state.cancelLoadCombatPreset() },
        )
    }
}

private val CombatTrackerState.hasCharacters: Boolean get() = sortedCharacters.isNotEmpty()

@Composable
private fun Header(state: CombatTrackerState, maxHeight: Dp, isCompact: Boolean) {
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Combat Tracker", color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Box(
                        modifier = Modifier
                            .background(oklch(0.30f, 0.07f, 70f, 0.35f), RoundedCornerShape(999.dp))
                            .border(BorderStroke(1.dp, oklch(0.55f, 0.1f, 70f, 0.6f)), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text("Round ${state.round}", color = oklch(0.85f, 0.1f, 70f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                    }
                }
                BurgerMenuButton(state = state, showJump = showJump)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 0.dp).padding(bottom = 12.dp)) {
                GradientPillButton(text = "Tour suivant", onClick = { state.nextTurn() }, fontSize = 13.5.sp, modifier = Modifier.fillMaxWidth(), trailingIcon = Icons.AutoMirrored.Filled.ArrowForward)
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
                    Text("Combat Tracker", color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Box(
                        modifier = Modifier
                            .background(oklch(0.30f, 0.07f, 70f, 0.35f), RoundedCornerShape(999.dp))
                            .border(BorderStroke(1.dp, oklch(0.55f, 0.1f, 70f, 0.6f)), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text("Round ${state.round}", color = oklch(0.85f, 0.1f, 70f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                    }
                    state.activeCharacter?.let { active ->
                        Row(modifier = Modifier.wrapContentWidth()) {
                            Text("Actif : ", color = oklch(0.65f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp, maxLines = 1)
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
                            text = "Voir le tour actif", onClick = { state.jumpToActive() },
                            textColor = oklch(0.80f, 0.1f, 70f), background = androidx.compose.ui.graphics.Color.Transparent,
                            borderColor = oklch(0.50f, 0.09f, 70f, 0.7f), fontSize = 12.sp, shape = RoundedCornerShape(999.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 11.dp, vertical = 4.dp),
                            leadingIcon = Icons.Default.Refresh,
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                    l.label, color = if (active) oklch(0.15f, 0.02f, 60f) else oklch(0.65f, 0.02f, 70f),
                                    fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
                                )
                            }
                        }
                    }
                    PillButton(
                        text = "Preset de personnages", onClick = { state.openPresets() },
                        textColor = oklch(0.85f, 0.02f, 80f), background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                    )
                    PillButton(
                        text = "Presets de combats", onClick = { state.openCombatPresets() },
                        textColor = oklch(0.85f, 0.02f, 80f), background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                    )
                    PillButton(
                        text = "Sauvegarder combat", onClick = { state.openSaveCombatDialog() },
                        textColor = if (state.hasCharacters) oklch(0.85f, 0.02f, 80f) else oklch(0.45f, 0.02f, 70f),
                        background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                        enabled = state.hasCharacters,
                    )
                    PillButton(
                        text = "+ Personnage", onClick = { state.openAddForm() },
                        textColor = oklch(0.85f, 0.02f, 80f), background = oklch(0.24f, 0.02f, 55f),
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.sp,
                    )
                    GradientPillButton(text = "Tour suivant", onClick = { state.nextTurn() }, fontSize = 13.5.sp, trailingIcon = Icons.AutoMirrored.Filled.ArrowForward)
                    PillButton(
                        text = "Vider", onClick = { state.requestClearAll() },
                        textColor = oklch(0.55f, 0.1f, 25f), background = androidx.compose.ui.graphics.Color.Transparent,
                        borderColor = oklch(0.40f, 0.08f, 25f, 0.6f), fontSize = 12.5.sp,
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(oklch(0.28f, 0.02f, 55f)))
    }
}

@Composable
private fun BurgerMenuButton(state: CombatTrackerState, showJump: Boolean) {
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
                DropdownMenuItem(text = { Text("Actif : ${active.name}", fontWeight = FontWeight.SemiBold) }, onClick = {}, enabled = false)
            }
            if (showJump) {
                DropdownMenuItem(
                    text = { Text("Voir le tour actif") },
                    leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    onClick = { state.jumpToActive(); expanded = false },
                )
            }
            HorizontalDivider()
            Layout.entries.forEach { l ->
                val active = state.layout == l
                DropdownMenuItem(
                    text = { Text(l.label) },
                    leadingIcon = if (active) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null,
                    onClick = { state.changeLayout(l); expanded = false },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(text = { Text("Preset de personnages") }, onClick = { state.openPresets(); expanded = false })
            DropdownMenuItem(text = { Text("Presets de combats") }, onClick = { state.openCombatPresets(); expanded = false })
            DropdownMenuItem(
                text = { Text("Sauvegarder combat") }, enabled = state.hasCharacters,
                onClick = { state.openSaveCombatDialog(); expanded = false },
            )
            DropdownMenuItem(text = { Text("+ Personnage") }, onClick = { state.openAddForm(); expanded = false })
            DropdownMenuItem(text = { Text("Vider") }, onClick = { state.requestClearAll(); expanded = false })
        }
    }
}

@Composable
private fun EmptyState(state: CombatTrackerState) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 90.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Aucun combattant", color = oklch(0.70f, 0.03f, 75f), fontFamily = Fonts.display, fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Text(
            "Ajoutez les personnages et monstres pour démarrer le combat.", color = oklch(0.55f, 0.02f, 70f),
            fontFamily = Fonts.body, fontSize = 14.sp, modifier = Modifier.padding(bottom = 18.dp),
        )
        PillButton(
            text = "+ Ajouter un personnage", onClick = { state.openAddForm() },
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
