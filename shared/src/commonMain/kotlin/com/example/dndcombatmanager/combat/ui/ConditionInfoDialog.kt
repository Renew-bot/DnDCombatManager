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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

@Composable
fun ConditionInfoDialog(name: String, description: List<String>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 440.dp)
                .background(oklch(0.20f, 0.02f, 55f), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, oklch(0.32f, 0.02f, 55f)), RoundedCornerShape(14.dp))
                .padding(22.dp),
        ) {
            Column {
                Text(
                    name, color = oklch(0.88f, 0.05f, 70f), fontFamily = Fonts.body,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 20.dp)) {
                    description.forEach { line ->
                        Text("•  $line", color = oklch(0.82f, 0.02f, 80f), fontFamily = Fonts.body, fontSize = 13.5.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f))
                    PillButton(
                        text = "Fermer", onClick = onDismiss,
                        textColor = oklch(0.16f, 0.02f, 60f), background = oklch(0.60f, 0.15f, 25f),
                        fontWeight = FontWeight.Bold, fontSize = 13.5.sp,
                    )
                }
            }
        }
    }
}
