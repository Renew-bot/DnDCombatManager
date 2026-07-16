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
import com.example.dndcombatmanager.combat.i18n.strings
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

/** Stands in for the original design's window.confirm(), which has no KMP equivalent. */
@Composable
fun ConfirmDialog(message: String, confirmLabel: String = strings().defaultConfirmLabel, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 420.dp)
                .background(oklch(0.20f, 0.02f, 55f), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, oklch(0.32f, 0.02f, 55f)), RoundedCornerShape(14.dp))
                .padding(22.dp),
        ) {
            Column {
                Text(message, color = oklch(0.88f, 0.02f, 80f), fontFamily = Fonts.body, fontSize = 14.sp, modifier = Modifier.padding(bottom = 20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f))
                    PillButton(
                        text = strings().cancelLabel, onClick = onDismiss,
                        textColor = oklch(0.70f, 0.02f, 70f), background = androidx.compose.ui.graphics.Color.Transparent,
                        borderColor = oklch(0.36f, 0.02f, 55f), fontSize = 13.5.sp,
                    )
                    PillButton(
                        text = confirmLabel, onClick = onConfirm,
                        textColor = oklch(0.16f, 0.02f, 60f), background = oklch(0.60f, 0.15f, 25f),
                        fontWeight = FontWeight.Bold, fontSize = 13.5.sp,
                    )
                }
            }
        }
    }
}
