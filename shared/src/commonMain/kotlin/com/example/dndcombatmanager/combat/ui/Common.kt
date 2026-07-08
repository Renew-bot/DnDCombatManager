package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier.padding(bottom = 8.dp),
        color = oklch(0.58f, 0.02f, 70f),
        fontFamily = Fonts.body,
        fontSize = 11.sp,
        letterSpacing = 0.9.sp,
    )
}

fun hpColor(pct: Float): Color = when {
    pct >= 0.5f -> oklch(0.65f, 0.15f, 145f)
    pct >= 0.25f -> oklch(0.74f, 0.14f, 75f)
    else -> oklch(0.60f, 0.19f, 25f)
}

@Composable
fun HpBar(pct: Float, modifier: Modifier = Modifier, height: androidx.compose.ui.unit.Dp = 10.dp, trackColor: Color = oklch(0.24f, 0.02f, 60f)) {
    Box(
        modifier = modifier
            .height(height)
            .background(trackColor, RoundedCornerShape(height / 2)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(pct.coerceIn(0f, 1f))
                .height(height)
                .background(hpColor(pct), RoundedCornerShape(height / 2)),
        )
    }
}
