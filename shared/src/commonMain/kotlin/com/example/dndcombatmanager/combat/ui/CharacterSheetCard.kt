package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatmanager.combat.model.CONDITIONS
import com.example.dndcombatmanager.combat.model.Character
import com.example.dndcombatmanager.combat.model.CharacterType
import com.example.dndcombatmanager.combat.model.SaveKey
import com.example.dndcombatmanager.combat.model.formatMod
import com.example.dndcombatmanager.combat.model.healthPct
import com.example.dndcombatmanager.combat.model.label
import com.example.dndcombatmanager.combat.model.speedText
import com.example.dndcombatmanager.combat.state.ResourceKey
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch
import kotlinx.coroutines.delay

private data class TypeMeta(val color: Color, val bg: Color, val border: Color)

private fun typeMeta(type: CharacterType): TypeMeta = when (type) {
    CharacterType.PJ -> TypeMeta(oklch(0.75f, 0.1f, 195f), oklch(0.28f, 0.05f, 195f, 0.35f), oklch(0.55f, 0.08f, 195f, 0.7f))
    CharacterType.MONSTRE -> TypeMeta(oklch(0.75f, 0.02f, 70f), oklch(0.28f, 0.01f, 60f, 0.5f), oklch(0.45f, 0.02f, 60f, 0.7f))
    CharacterType.BOSS -> TypeMeta(oklch(0.78f, 0.14f, 40f), oklch(0.28f, 0.06f, 30f, 0.4f), oklch(0.55f, 0.12f, 30f, 0.7f))
}

@Composable
fun CharacterSheetCard(
    character: Character,
    isActive: Boolean,
    onDamage: (String, Int) -> Unit,
    onHeal: (String, Int) -> Unit,
    onTempHp: (String, Int) -> Unit,
    onToggleCondition: (String, String) -> Unit,
    onToggleResource: (String, ResourceKey) -> Unit,
    onLegendaryUse: (String, Int) -> Unit,
    onLegendaryResUse: (String, Int) -> Unit,
    onSetExhaustion: (String, Int) -> Unit,
    onNotes: (String, String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onSavePreset: (Character) -> Unit,
    modifier: Modifier = Modifier,
) {
    val meta = typeMeta(character.type)
    var amount by remember(character.id) { mutableStateOf("") }
    var presetSaved by remember(character.id) { mutableStateOf(false) }

    LaunchedEffectPresetSaved(presetSaved) { presetSaved = false }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(oklch(0.23f, 0.025f, 55f), oklch(0.19f, 0.02f, 55f))),
                    RoundedCornerShape(16.dp),
                )
                .border(
                    BorderStroke(if (isActive) 1.5.dp else 1.dp, if (isActive) oklch(0.70f, 0.14f, 70f) else oklch(0.30f, 0.02f, 55f)),
                    RoundedCornerShape(16.dp),
                )
                .padding(22.dp),
        ) {
            // Header row: initiative badge, name/type, action buttons
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(character.initiative.toString(), color = oklch(0.80f, 0.14f, 70f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Column {
                        Text(
                            character.name,
                            color = if (character.type == CharacterType.PJ) oklch(0.99f, 0.02f, 80f) else oklch(0.94f, 0.02f, 80f),
                            fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 22.sp,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .background(meta.bg, RoundedCornerShape(999.dp))
                                .border(BorderStroke(1.dp, meta.border), RoundedCornerShape(999.dp))
                                .padding(horizontal = 9.dp, vertical = 2.dp),
                        ) {
                            Text(character.type.label(), color = meta.color, fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PillButton(
                        text = if (presetSaved) "Enregistré ✓" else "Préset",
                        onClick = { onSavePreset(character); presetSaved = true },
                        textColor = if (presetSaved) oklch(0.85f, 0.1f, 145f) else oklch(0.75f, 0.02f, 70f),
                        background = if (presetSaved) oklch(0.30f, 0.06f, 145f, 0.4f) else oklch(0.21f, 0.02f, 55f),
                        borderColor = if (presetSaved) oklch(0.55f, 0.11f, 145f, 0.8f) else oklch(0.34f, 0.02f, 55f),
                        fontSize = 11.5.sp,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    )
                    IconSquareButton(text = "✎", danger = false, onClick = { onEdit(character.id) })
                    IconSquareButton(text = "✕", danger = true, onClick = { onDelete(character.id) })
                }
            }

            Spacer(18.dp)

            // HP block
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(character.currentHp.toString(), color = oklch(0.94f, 0.02f, 80f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 26.sp)
                Text("/ ${character.maxHp} PV", color = oklch(0.60f, 0.02f, 70f), fontFamily = Fonts.mono, fontSize = 15.sp)
                if (character.tempHp > 0) {
                    Box(
                        modifier = Modifier
                            .background(oklch(0.30f, 0.05f, 210f, 0.4f), RoundedCornerShape(6.dp))
                            .border(BorderStroke(1.dp, oklch(0.50f, 0.08f, 210f, 0.6f)), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 1.dp),
                    ) {
                        Text("+${character.tempHp} temp", color = oklch(0.78f, 0.1f, 210f), fontFamily = Fonts.mono, fontSize = 13.sp)
                    }
                }
            }
            Spacer(6.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(oklch(0.24f, 0.02f, 60f), RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(6.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(healthPct(character.currentHp, character.maxHp))
                        .height(10.dp)
                        .background(hpColor(healthPct(character.currentHp, character.maxHp)), RoundedCornerShape(6.dp)),
                )
            }

            Spacer(16.dp)

            // Damage / heal row
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                DarkTextField(
                    value = amount, onValueChange = { amount = it.filter { ch -> ch.isDigit() } },
                    placeholder = "0", textAlign = TextAlign.Center,
                    background = oklch(0.19f, 0.02f, 55f),
                    modifier = Modifier.width(64.dp),
                )
                PillButton(
                    text = "− Dégâts",
                    onClick = { val n = amount.toIntOrNull() ?: 0; if (n > 0) onDamage(character.id, n) },
                    textColor = oklch(0.85f, 0.1f, 25f), background = oklch(0.28f, 0.09f, 25f, 0.5f),
                    borderColor = oklch(0.48f, 0.14f, 25f, 0.8f), fontSize = 13.sp,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 7.dp),
                )
                PillButton(
                    text = "+ Soin",
                    onClick = { val n = amount.toIntOrNull() ?: 0; if (n > 0) onHeal(character.id, n) },
                    textColor = oklch(0.85f, 0.1f, 145f), background = oklch(0.28f, 0.07f, 145f, 0.5f),
                    borderColor = oklch(0.48f, 0.1f, 145f, 0.8f), fontSize = 13.sp,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 7.dp),
                )
                Box(modifier = Modifier.weight(1f))
                Text("PV TEMP", color = oklch(0.60f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 11.sp, letterSpacing = 0.4.sp)
                DarkNumberField(
                    value = character.tempHp, onValueChange = { onTempHp(character.id, it) },
                    textAlign = TextAlign.Center, background = oklch(0.19f, 0.02f, 55f), textColor = oklch(0.78f, 0.1f, 210f),
                    modifier = Modifier.width(56.dp),
                )
            }

            Spacer(16.dp)

            // Stat chips: AC + speed
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatChip(label = "CA", value = character.ac.toString(), modifier = Modifier.weight(1f))
                StatChip(label = "Vitesse", value = speedText(character), modifier = Modifier.weight(2f), valueFontSize = 14.sp)
            }

            Spacer(16.dp)

            SectionLabel("Jets de sauvegarde")
            SaveKey.entries.chunked(6).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(7.dp))
                                .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(7.dp))
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(key.label, color = oklch(0.55f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 10.sp)
                                Text(formatMod(character.saves.get(key)), color = oklch(0.88f, 0.02f, 80f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(16.dp)

            SectionLabel("Ressources du tour")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ResourcePill("Action", character.action) { onToggleResource(character.id, ResourceKey.ACTION) }
                ResourcePill("Bonus", character.bonus) { onToggleResource(character.id, ResourceKey.BONUS) }
                ResourcePill("Réaction", character.reaction) { onToggleResource(character.id, ResourceKey.REACTION) }
            }

            if (character.legendaryMax > 0) {
                Spacer(16.dp)
                SectionLabel("Actions légendaires (${character.legendaryCurrent}/${character.legendaryMax})")
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    repeat(character.legendaryMax) { i ->
                        val filled = i < character.legendaryCurrent
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(if (filled) oklch(0.60f, 0.14f, 40f) else oklch(0.21f, 0.02f, 55f), RoundedCornerShape(7.dp))
                                .border(BorderStroke(1.dp, if (filled) oklch(0.75f, 0.14f, 40f) else oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(7.dp))
                                .clickable { onLegendaryUse(character.id, if (filled) -1 else 1) },
                        )
                    }
                }
            }

            if (character.legendaryResMax > 0) {
                Spacer(16.dp)
                SectionLabel("Résistances légendaires (${character.legendaryResCurrent}/${character.legendaryResMax})")
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    repeat(character.legendaryResMax) { i ->
                        val filled = i < character.legendaryResCurrent
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(if (filled) oklch(0.60f, 0.1f, 200f) else oklch(0.21f, 0.02f, 55f), RoundedCornerShape(999.dp))
                                .border(BorderStroke(1.dp, if (filled) oklch(0.75f, 0.1f, 200f) else oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(999.dp))
                                .clickable { onLegendaryResUse(character.id, if (filled) -1 else 1) },
                        )
                    }
                }
            }

            Spacer(16.dp)

            SectionLabel("États")
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                CONDITIONS.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        pair.forEach { name ->
                            val checked = character.conditions.contains(name)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f).clickable { onToggleCondition(character.id, name) },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(if (checked) oklch(0.55f, 0.12f, 70f) else oklch(0.19f, 0.02f, 55f), RoundedCornerShape(5.dp))
                                        .border(BorderStroke(1.dp, if (checked) oklch(0.70f, 0.13f, 70f) else oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(5.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (checked) Text("✓", color = oklch(0.15f, 0.02f, 60f), fontSize = 12.sp)
                                }
                                Text(name, color = if (checked) oklch(0.88f, 0.05f, 70f) else oklch(0.65f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp)
                            }
                        }
                        if (pair.size == 1) Box(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(16.dp)

            SectionLabel("Niveau d'épuisement")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (level in 0..6) {
                    val filled = level > 0 && level <= character.exhaustion
                    val isCurrent = character.exhaustion == level
                    val (bg, border, textColor) = when {
                        filled -> Triple(oklch(0.50f, 0.15f, 30f), oklch(0.65f, 0.16f, 30f), oklch(0.93f, 0.03f, 60f))
                        isCurrent -> Triple(oklch(0.30f, 0.05f, 145f), oklch(0.60f, 0.12f, 145f), oklch(0.85f, 0.1f, 145f))
                        else -> Triple(oklch(0.21f, 0.02f, 55f), oklch(0.34f, 0.02f, 55f), oklch(0.55f, 0.02f, 70f))
                    }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(bg, RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, border), RoundedCornerShape(8.dp))
                            .clickable { onSetExhaustion(character.id, level) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(level.toString(), color = textColor, fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(12.dp)
            SectionLabel("Notes / Concentration")
            DarkTextArea(
                value = character.notes, onValueChange = { onNotes(character.id, it) },
                placeholder = "Sort concentré, tactique, remarque…",
            )
        }

        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 24.dp, y = (-11).dp)
                    .background(Brush.linearGradient(listOf(oklch(0.74f, 0.16f, 70f), oklch(0.64f, 0.17f, 55f))), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text("TOUR ACTUEL", color = oklch(0.18f, 0.02f, 60f), fontFamily = Fonts.body, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.8.sp)
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, modifier: Modifier = Modifier, valueFontSize: androidx.compose.ui.unit.TextUnit = 16.sp) {
    Box(
        modifier = modifier
            .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(9.dp))
            .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(9.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label.uppercase(), color = oklch(0.88f, 0.02f, 80f, 0.6f), fontFamily = Fonts.body, fontSize = 10.sp, letterSpacing = 0.5.sp)
            Text(value, color = oklch(0.88f, 0.02f, 80f), fontFamily = Fonts.mono, fontWeight = FontWeight.SemiBold, fontSize = valueFontSize, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ResourcePill(label: String, available: Boolean, onClick: () -> Unit) {
    PillButton(
        text = "${if (available) "●" else "○"} $label",
        onClick = onClick,
        textColor = if (available) oklch(0.90f, 0.1f, 75f) else oklch(0.55f, 0.02f, 70f),
        background = if (available) oklch(0.38f, 0.1f, 70f, 0.5f) else oklch(0.21f, 0.02f, 55f),
        borderColor = if (available) oklch(0.65f, 0.13f, 70f, 0.9f) else oklch(0.32f, 0.02f, 55f),
        fontSize = 13.sp,
        shape = RoundedCornerShape(999.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 7.dp),
    )
}

@Composable
private fun IconSquareButton(text: String, danger: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(oklch(0.21f, 0.02f, 55f), RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = if (danger) oklch(0.65f, 0.14f, 25f) else oklch(0.75f, 0.02f, 70f), fontSize = 13.sp)
    }
}

@Composable
private fun Spacer(height: androidx.compose.ui.unit.Dp) {
    Box(modifier = Modifier.height(height))
}

@Composable
private fun LaunchedEffectPresetSaved(active: Boolean, onTimeout: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(active) {
        if (active) {
            delay(1600)
            onTimeout()
        }
    }
}
