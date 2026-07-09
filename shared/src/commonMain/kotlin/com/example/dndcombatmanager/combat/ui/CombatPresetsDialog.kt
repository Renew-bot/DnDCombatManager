package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatmanager.combat.model.metaLabel
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

@Composable
fun CombatPresetsDialog(state: CombatTrackerState) {
    if (!state.showCombatPresets) return

    Dialog(onDismissRequest = { state.closeCombatPresets() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(oklch(0.10f, 0.01f, 60f, 0.75f))
                .clickable { state.closeCombatPresets() }
                .padding(vertical = 40.dp, horizontal = 20.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 620.dp)
                    .fillMaxWidth()
                    .heightIn(max = 640.dp)
                    .consumeClicks()
                    .background(oklch(0.20f, 0.02f, 55f), RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, oklch(0.32f, 0.02f, 55f)), RoundedCornerShape(16.dp))
                    .padding(26.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
                ) {
                    Text("Presets de combats", color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    PillButton(
                        text = "Fermer", onClick = { state.closeCombatPresets() },
                        textColor = oklch(0.70f, 0.02f, 70f), background = androidx.compose.ui.graphics.Color.Transparent,
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.5.sp,
                    )
                }

                if (state.combatPresets.isEmpty()) {
                    Text(
                        "Aucun preset de combat enregistré. Cliquez \"Sauvegarder combat\" pour enregistrer la liste actuelle de combattants.",
                        color = oklch(0.50f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp, horizontal = 10.dp),
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                    ) {
                        state.combatPresets.forEach { preset ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(11.dp))
                                    .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(11.dp))
                                    .padding(horizontal = 15.dp, vertical = 13.dp),
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(preset.name, color = oklch(0.90f, 0.02f, 80f), fontFamily = Fonts.body, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                                    Text(
                                        preset.metaLabel(), color = oklch(0.60f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 12.sp,
                                        maxLines = 2, modifier = Modifier.padding(top = 2.dp),
                                    )
                                }
                                GradientPillButton(
                                    text = "Charger",
                                    onClick = { state.requestLoadCombatPreset(preset.id) },
                                    fontSize = 12.5.sp,
                                    contentPadding = PaddingValues(horizontal = 13.dp, vertical = 7.dp),
                                    shape = RoundedCornerShape(8.dp),
                                )
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(oklch(0.21f, 0.02f, 55f), RoundedCornerShape(8.dp))
                                        .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(8.dp))
                                        .clickable { state.requestDeleteCombatPreset(preset.id) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = oklch(0.65f, 0.14f, 25f), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
