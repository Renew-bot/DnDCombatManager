package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatmanager.combat.i18n.strings
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

/**
 * A prone target's advantage/disadvantage against attacks depends on the attacker's range
 * (D&D 5e: <= 1,50 m is advantage, further is disadvantage), which this app doesn't track,
 * so it's asked for here right before the attack roll.
 */
@Composable
fun ProneDistanceDialog(state: CombatTrackerState) {
    val s = strings()
    val targetId = state.pendingProneTargetId ?: return
    val attackerId = state.pendingAttack?.attackerId
    val target = state.characters.find { it.id == targetId }
    val attacker = state.characters.find { it.id == attackerId }
    var text by remember(targetId) { mutableStateOf(s.distancePlaceholder) }
    val distance = text.replace(",", ".").toDoubleOrNull()

    Dialog(onDismissRequest = { state.cancelProneDistance() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 380.dp)
                .background(oklch(0.20f, 0.02f, 55f), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, oklch(0.32f, 0.02f, 55f)), RoundedCornerShape(14.dp))
                .padding(22.dp),
        ) {
            Column {
                Text(
                    s.proneTitle(target?.name ?: s.proneTargetFallback), color = oklch(0.88f, 0.05f, 70f), fontFamily = Fonts.body,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    s.proneQuestion(attacker?.name ?: s.proneAttackerFallback, target?.name ?: s.proneTargetFallback),
                    color = oklch(0.72f, 0.02f, 80f), fontFamily = Fonts.body, fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                FieldLabel(s.distanceLabel) {
                    DarkTextField(value = text, onValueChange = { text = it }, placeholder = s.distancePlaceholder)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Box(modifier = Modifier.weight(1f))
                    PillButton(
                        text = s.cancelLabel, onClick = { state.cancelProneDistance() },
                        textColor = oklch(0.70f, 0.02f, 70f), background = androidx.compose.ui.graphics.Color.Transparent,
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.5.sp,
                    )
                    GradientPillButton(
                        text = s.rollBtn,
                        onClick = { state.confirmProneDistance(distance ?: 1.5) },
                    )
                }
            }
        }
    }
}
