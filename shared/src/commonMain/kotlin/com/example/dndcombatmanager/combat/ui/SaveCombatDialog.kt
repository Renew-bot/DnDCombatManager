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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

@Composable
fun SaveCombatDialog(state: CombatTrackerState) {
    if (!state.showSaveCombatDialog) return

    Dialog(onDismissRequest = { state.closeSaveCombatDialog() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(oklch(0.10f, 0.01f, 60f, 0.75f))
                .clickable { state.closeSaveCombatDialog() }
                .padding(vertical = 40.dp, horizontal = 20.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxWidth()
                    .consumeClicks()
                    .background(oklch(0.20f, 0.02f, 55f), RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, oklch(0.32f, 0.02f, 55f)), RoundedCornerShape(16.dp))
                    .padding(26.dp),
            ) {
                Text(
                    "Sauvegarder le combat", color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 19.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    "Enregistre la liste actuelle de combattants comme preset de combat réutilisable.",
                    color = oklch(0.60f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 18.dp),
                )
                FieldLabel("Nom du preset de combat", modifier = Modifier.fillMaxWidth().padding(bottom = 22.dp)) {
                    DarkTextField(
                        value = state.saveCombatName, onValueChange = { state.changeSaveCombatName(it) },
                        placeholder = "Ex. Embuscade de gobelins",
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f))
                    PillButton(
                        text = "Annuler", onClick = { state.closeSaveCombatDialog() },
                        textColor = oklch(0.70f, 0.02f, 70f), background = androidx.compose.ui.graphics.Color.Transparent,
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.5.sp,
                    )
                    GradientPillButton(text = "Sauvegarder", onClick = { state.confirmSaveCombat() })
                }
            }
        }
    }
}
