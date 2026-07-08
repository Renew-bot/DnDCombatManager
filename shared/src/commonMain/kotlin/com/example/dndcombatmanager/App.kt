package com.example.dndcombatmanager

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dndcombatmanager.combat.ui.CombatTrackerScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        CombatTrackerScreen()
    }
}
