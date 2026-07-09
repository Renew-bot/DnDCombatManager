package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatmanager.combat.model.Character
import com.example.dndcombatmanager.combat.model.CharacterType
import com.example.dndcombatmanager.combat.model.healthPct
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import com.example.dndcombatmanager.combat.state.PendingAttack
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

/** True while an attack is waiting for a target and this character is a valid one to pick. */
private fun isTargetable(pending: PendingAttack?, character: Character): Boolean =
    pending != null && character.id != pending.attackerId

@Composable
private fun TargetingBanner(pending: PendingAttack, onCancel: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(oklch(0.30f, 0.09f, 25f, 0.35f), RoundedCornerShape(10.dp))
            .border(BorderStroke(1.dp, oklch(0.55f, 0.13f, 25f, 0.7f)), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            "Choisissez une cible pour « ${pending.attackName} »",
            color = oklch(0.85f, 0.1f, 25f), fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
            modifier = Modifier.weight(1f),
        )
        PillButton(
            text = "Annuler", onClick = onCancel,
            textColor = oklch(0.80f, 0.02f, 70f), background = Color.Transparent, borderColor = oklch(0.45f, 0.02f, 55f),
            fontSize = 12.sp, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp), shape = RoundedCornerShape(7.dp),
        )
    }
}

/** Renders a character's sheet and attacks panel side-by-side, or stacked on narrow widths. */
@Composable
fun CharacterAndAttacksPane(
    character: Character,
    isActive: Boolean,
    state: CombatTrackerState,
    stacked: Boolean,
    modifier: Modifier = Modifier,
) {
    if (stacked) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CharacterSheetCard(
                character = character, isActive = isActive,
                onDamage = state::handleDamage, onHeal = state::handleHeal, onTempHp = state::handleTempHp,
                onToggleCondition = state::handleToggleCondition, onToggleResource = state::handleToggleResource,
                onLegendaryUse = state::handleLegendaryUse, onLegendaryResUse = state::handleLegendaryResUse,
                onSetExhaustion = state::handleSetExhaustion, onNotes = state::handleNotes,
                onEdit = state::openEditForm, onDelete = state::requestDelete, onSavePreset = state::handleSavePreset,
                modifier = Modifier.fillMaxWidth(),
            )
            AttacksPanelCard(
                character = character, rolls = state.rollsFor(character.id), targetingActive = state.pendingAttack != null,
                isOwnTurn = isActive,
                onSave = state::handleSaveAttack, onDelete = state::handleDeleteAttack,
                onUse = state::beginAttack, modifier = Modifier.fillMaxWidth(),
            )
        }
    } else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            CharacterSheetCard(
                character = character, isActive = isActive,
                onDamage = state::handleDamage, onHeal = state::handleHeal, onTempHp = state::handleTempHp,
                onToggleCondition = state::handleToggleCondition, onToggleResource = state::handleToggleResource,
                onLegendaryUse = state::handleLegendaryUse, onLegendaryResUse = state::handleLegendaryResUse,
                onSetExhaustion = state::handleSetExhaustion, onNotes = state::handleNotes,
                onEdit = state::openEditForm, onDelete = state::requestDelete, onSavePreset = state::handleSavePreset,
                modifier = Modifier.weight(1.7f),
            )
            AttacksPanelCard(
                character = character, rolls = state.rollsFor(character.id), targetingActive = state.pendingAttack != null,
                isOwnTurn = isActive,
                onSave = state::handleSaveAttack, onDelete = state::handleDeleteAttack,
                onUse = state::beginAttack, modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun SidebarLayout(state: CombatTrackerState, isNarrow: Boolean, stackAttacks: Boolean, modifier: Modifier = Modifier) {
    val sorted = state.sortedCharacters
    val viewingId = state.effectiveViewingId
    val viewingChar = state.viewingCharacter
    val pending = state.pendingAttack

    val list = @Composable { listModifier: Modifier ->
        Column(modifier = listModifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            sorted.forEach { c ->
                SidebarRow(
                    character = c,
                    isActive = c.id == state.activeId,
                    isViewing = c.id == viewingId,
                    targetable = isTargetable(pending, c),
                    onClick = { state.handleCharacterClick(c.id) },
                )
            }
        }
    }

    if (isNarrow) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            pending?.let { TargetingBanner(it, onCancel = state::cancelAttack) }
            list(Modifier.fillMaxWidth())
            viewingChar?.let { CharacterAndAttacksPane(it, it.id == state.activeId, state, stackAttacks) }
        }
    } else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(modifier = Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pending?.let { TargetingBanner(it, onCancel = state::cancelAttack) }
                list(Modifier.fillMaxWidth())
            }
            viewingChar?.let { CharacterAndAttacksPane(it, it.id == state.activeId, state, stackAttacks, modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun SidebarRow(character: Character, isActive: Boolean, isViewing: Boolean, targetable: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    isViewing -> oklch(0.27f, 0.03f, 55f)
                    targetable -> oklch(0.30f, 0.09f, 25f, 0.35f)
                    else -> Color.Transparent
                },
                RoundedCornerShape(10.dp),
            )
            .border(
                BorderStroke(
                    1.dp,
                    when {
                        targetable -> oklch(0.62f, 0.16f, 25f, 0.9f)
                        isActive -> oklch(0.60f, 0.12f, 70f, 0.8f)
                        else -> Color.Transparent
                    },
                ),
                RoundedCornerShape(10.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        InitBadge(character.initiative)
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    character.name,
                    color = if (character.type == CharacterType.PJ) oklch(0.95f, 0.02f, 80f) else oklch(0.90f, 0.02f, 80f),
                    fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false),
                )
                if (character.conditions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(oklch(0.35f, 0.1f, 30f, 0.4f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        Text(character.conditions.size.toString(), color = oklch(0.85f, 0.1f, 30f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
                    }
                }
            }
            Box(modifier = Modifier.padding(top = 5.dp)) {
                HpBar(healthPct(character.currentHp, character.maxHp), height = 5.dp)
            }
        }
    }
}

@Composable
private fun InitBadge(initiative: Int, size: androidx.compose.ui.unit.Dp = 26.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(7.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(initiative.toString(), color = oklch(0.78f, 0.1f, 70f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
fun TimelineLayout(state: CombatTrackerState, isNarrow: Boolean, stackAttacks: Boolean, modifier: Modifier = Modifier) {
    val sorted = state.sortedCharacters
    val viewingId = state.effectiveViewingId
    val viewingChar = state.viewingCharacter
    val pending = state.pendingAttack

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        pending?.let { TargetingBanner(it, onCancel = state::cancelAttack) }
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            sorted.forEach { c ->
                TimelineCard(
                    character = c, isActive = c.id == state.activeId, isViewing = c.id == viewingId,
                    targetable = isTargetable(pending, c),
                    onClick = { state.handleCharacterClick(c.id) },
                )
            }
        }
        viewingChar?.let { CharacterAndAttacksPane(it, it.id == state.activeId, state, stackAttacks) }
    }
}

@Composable
private fun TimelineCard(character: Character, isActive: Boolean, isViewing: Boolean, targetable: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .wrapContentWidth()
            .background(
                when {
                    isViewing -> oklch(0.27f, 0.03f, 55f)
                    targetable -> oklch(0.30f, 0.09f, 25f, 0.35f)
                    else -> oklch(0.20f, 0.02f, 55f)
                },
                RoundedCornerShape(12.dp),
            )
            .border(
                BorderStroke(
                    1.dp,
                    when {
                        targetable -> oklch(0.62f, 0.16f, 25f, 0.9f)
                        isActive -> oklch(0.60f, 0.12f, 70f, 0.8f)
                        else -> oklch(0.30f, 0.02f, 55f)
                    },
                ),
                RoundedCornerShape(12.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        InitBadge(character.initiative)
        Column {
            Text(
                character.name,
                color = if (character.type == CharacterType.PJ) oklch(0.93f, 0.02f, 80f) else oklch(0.88f, 0.02f, 80f),
                fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(110.dp),
            )
            Box(modifier = Modifier.padding(top = 4.dp)) {
                HpBar(healthPct(character.currentHp, character.maxHp), height = 5.dp, modifier = Modifier.width(90.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FocusLayout(state: CombatTrackerState, isNarrow: Boolean, stackAttacks: Boolean, modifier: Modifier = Modifier) {
    val activeChar = state.activeCharacter
    val others = state.sortedCharacters.filter { it.id != state.activeId }
    val pending = state.pendingAttack

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        pending?.let { TargetingBanner(it, onCancel = state::cancelAttack) }
        activeChar?.let { CharacterAndAttacksPane(it, true, state, stackAttacks, modifier = Modifier.fillMaxWidth()) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "AUTRES COMBATTANTS — CLIQUER POUR CONSULTER",
                color = oklch(0.55f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 11.sp, letterSpacing = 0.7.sp,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                others.forEach { c ->
                    FocusTile(
                        character = c, isActive = c.id == state.activeId, targetable = isTargetable(pending, c),
                        onClick = { state.handleFocusTileClick(c.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusTile(character: Character, isActive: Boolean, targetable: Boolean, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .width(128.dp)
            .background(if (targetable) oklch(0.30f, 0.09f, 25f, 0.35f) else oklch(0.20f, 0.02f, 55f), RoundedCornerShape(10.dp))
            .border(
                BorderStroke(
                    1.dp,
                    when {
                        targetable -> oklch(0.62f, 0.16f, 25f, 0.9f)
                        isActive -> oklch(0.60f, 0.12f, 70f, 0.8f)
                        else -> oklch(0.30f, 0.02f, 55f)
                    },
                ),
                RoundedCornerShape(10.dp),
            )
            .clickable(enabled = !isActive) { onClick() }
            .padding(horizontal = 11.dp, vertical = 9.dp),
    ) {
        Text(character.initiative.toString(), color = oklch(0.78f, 0.1f, 70f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        Text(
            character.name,
            color = if (character.type == CharacterType.PJ) oklch(0.93f, 0.02f, 80f) else oklch(0.88f, 0.02f, 80f),
            fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
        HpBar(healthPct(character.currentHp, character.maxHp), height = 4.dp)
    }
}
