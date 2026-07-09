package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatmanager.combat.model.CharacterType
import com.example.dndcombatmanager.combat.model.SaveKey
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

@Composable
fun CharacterFormDialog(state: CombatTrackerState) {
    if (!state.showForm) return
    val fd = state.formData

    Dialog(onDismissRequest = { state.closeForm() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(oklch(0.10f, 0.01f, 60f, 0.75f))
                .clickable { state.closeForm() }
                .padding(vertical = 40.dp, horizontal = 20.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .consumeClicks()
                    .background(oklch(0.20f, 0.02f, 55f), RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, oklch(0.32f, 0.02f, 55f)), RoundedCornerShape(16.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(26.dp),
            ) {
                Text(
                    if (state.editingId != null) "Modifier le personnage" else "Ajouter un personnage",
                    color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 19.sp,
                    modifier = Modifier.padding(bottom = 18.dp),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    FieldLabel("Portrait") {
                        PortraitBox(
                            portrait = fd.portrait,
                            onImageBytes = { bytes -> state.formData = fd.copy(portrait = bytes.toPortraitString()) },
                            onClear = { state.formData = fd.copy(portrait = null) },
                        )
                    }
                    FieldLabel("Nom", modifier = Modifier.weight(2f)) {
                        DarkTextField(value = fd.name, onValueChange = { state.formData = fd.copy(name = it) }, placeholder = "Ex. Aeliana")
                    }
                    FieldLabel("Type", modifier = Modifier.weight(1f)) {
                        DarkSelectField(
                            selected = fd.type,
                            options = listOf(
                                SelectOption(CharacterType.PJ, "Joueur"),
                                SelectOption(CharacterType.MONSTRE, "Monstre"),
                                SelectOption(CharacterType.BOSS, "Créature légendaire"),
                            ),
                            onSelect = { state.formData = fd.copy(type = it) },
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    FieldLabel("Initiative", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.initiative, onValueChange = { state.formData = fd.copy(initiative = it) })
                    }
                    FieldLabel("PV max", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.maxHp, onValueChange = { state.formData = fd.copy(maxHp = it) })
                    }
                    FieldLabel("CA", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.ac, onValueChange = { state.formData = fd.copy(ac = it) })
                    }
                    FieldLabel("Vitesse marche (m)", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.speed, onValueChange = { state.formData = fd.copy(speed = it) })
                    }
                }

                SectionLabel("Vitesses spéciales (0 = aucune)", modifier = Modifier.padding(top = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    FieldLabel("Vol (m)", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.speedFly, onValueChange = { state.formData = fd.copy(speedFly = it) })
                    }
                    FieldLabel("Nage (m)", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.speedSwim, onValueChange = { state.formData = fd.copy(speedSwim = it) })
                    }
                    FieldLabel("Escalade (m)", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.speedClimb, onValueChange = { state.formData = fd.copy(speedClimb = it) })
                    }
                }

                SectionLabel("Modificateurs de sauvegarde", modifier = Modifier.padding(top = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    SaveKey.entries.forEach { key ->
                        FieldLabel(key.label, modifier = Modifier.weight(1f)) {
                            DarkNumberField(
                                value = fd.saves.get(key),
                                onValueChange = { state.formData = fd.copy(saves = fd.saves.with(key, it)) },
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    FieldLabel("Actions légendaires (max)", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.legendaryMax, onValueChange = { state.formData = fd.copy(legendaryMax = it) })
                    }
                    FieldLabel("Résistances légendaires (max)", modifier = Modifier.weight(1f)) {
                        DarkNumberField(value = fd.legendaryResMax, onValueChange = { state.formData = fd.copy(legendaryResMax = it) })
                    }
                }

                AttackListEditor(
                    attacks = fd.attacks,
                    onChange = { state.formData = fd.copy(attacks = it) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 22.dp),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier
                        .clickable { state.formData = fd.copy(saveAsPreset = !fd.saveAsPreset) }
                        .padding(bottom = 18.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .background(if (fd.saveAsPreset) oklch(0.60f, 0.13f, 70f) else oklch(0.19f, 0.02f, 55f), RoundedCornerShape(4.dp))
                            .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (fd.saveAsPreset) Icon(Icons.Default.Check, contentDescription = null, tint = oklch(0.15f, 0.02f, 60f), modifier = Modifier.size(12.dp))
                    }
                    Text("Enregistrer aussi comme préset réutilisable", color = oklch(0.75f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f))
                    PillButton(
                        text = "Annuler", onClick = { state.closeForm() },
                        textColor = oklch(0.70f, 0.02f, 70f), background = androidx.compose.ui.graphics.Color.Transparent,
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.5.sp,
                    )
                    GradientPillButton(
                        text = if (state.editingId != null) "Enregistrer" else "Ajouter",
                        onClick = { state.submitForm() },
                    )
                }
            }
        }
    }
}
