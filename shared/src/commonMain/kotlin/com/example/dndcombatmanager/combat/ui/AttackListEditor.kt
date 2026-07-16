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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatmanager.combat.i18n.LocalLanguage
import com.example.dndcombatmanager.combat.i18n.strings
import com.example.dndcombatmanager.combat.model.Attack
import com.example.dndcombatmanager.combat.model.AttackCost
import com.example.dndcombatmanager.combat.model.AttackStep
import com.example.dndcombatmanager.combat.model.StepType
import com.example.dndcombatmanager.combat.model.label
import com.example.dndcombatmanager.combat.model.stepPlaceholder
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

/**
 * Lets a character's attacks be authored inline in the add/edit character form, before the
 * character (and, if requested, its preset) is actually saved. Unlike [AttacksPanelCard] this
 * has no notion of a live combatant — no dice rolling, no action-economy "use" button — it just
 * edits a plain [Attack] list held in the form's draft state.
 */
@Composable
fun AttackListEditor(attacks: List<Attack>, onChange: (List<Attack>) -> Unit, modifier: Modifier = Modifier) {
    val ui = strings()
    val lang = LocalLanguage.current
    var building by remember { mutableStateOf(false) }
    var editingAttackId by remember { mutableStateOf<String?>(null) }
    var draftName by remember { mutableStateOf("") }
    var draftCost by remember { mutableStateOf(AttackCost.ACTION) }
    var draftSteps by remember { mutableStateOf(listOf(DraftStep())) }
    var idCounter by remember { mutableStateOf(0) }

    fun startAdd() {
        building = true
        editingAttackId = null
        draftName = ""
        draftCost = AttackCost.ACTION
        draftSteps = listOf(DraftStep())
    }

    fun startEdit(attack: Attack) {
        building = true
        editingAttackId = attack.id
        draftName = attack.name
        draftCost = attack.cost
        draftSteps = attack.steps.map { DraftStep(it.type, it.text) }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(ui.attacksHeader, modifier = Modifier.padding(bottom = 0.dp))
            if (!building) {
                PillButton(
                    text = ui.addAttackBtn, onClick = { startAdd() },
                    textColor = oklch(0.85f, 0.1f, 70f), background = oklch(0.30f, 0.06f, 70f, 0.35f),
                    borderColor = oklch(0.55f, 0.1f, 70f, 0.6f), fontSize = 12.5.sp,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), shape = RoundedCornerShape(8.dp),
                )
            }
        }

        Box(modifier = Modifier.height(10.dp))

        if (!building) {
            if (attacks.isEmpty()) {
                Text(
                    ui.noAttacksYet, color = oklch(0.50f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    attacks.forEach { attack ->
                        AttackSummaryRow(
                            attack = attack,
                            onEdit = { startEdit(attack) },
                            onDelete = { onChange(attacks.filter { it.id != attack.id }) },
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    FieldLabel(ui.attackNameLabel, modifier = Modifier.weight(2f)) {
                        DarkTextField(value = draftName, onValueChange = { draftName = it }, placeholder = ui.attackNamePlaceholder)
                    }
                    FieldLabel(ui.costLabel, modifier = Modifier.weight(1f)) {
                        DarkSelectField(
                            selected = draftCost,
                            options = AttackCost.entries.map { SelectOption(it, it.label(lang)) },
                            onSelect = { draftCost = it },
                        )
                    }
                }

                draftSteps.forEachIndexed { idx, row ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkSelectField(
                            selected = row.type,
                            options = StepType.entries.map { SelectOption(it, it.label(lang)) },
                            onSelect = { newType -> draftSteps = draftSteps.mapIndexed { i, step -> if (i == idx) step.copy(type = newType) else step } },
                            modifier = Modifier.width(128.dp),
                        )
                        DarkTextField(
                            value = row.text,
                            onValueChange = { text -> draftSteps = draftSteps.mapIndexed { i, step -> if (i == idx) step.copy(text = text) else step } },
                            placeholder = stepPlaceholder(row.type, lang),
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(oklch(0.21f, 0.02f, 55f), RoundedCornerShape(7.dp))
                                .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(7.dp))
                                .clickable(enabled = draftSteps.size > 1) { draftSteps = draftSteps.filterIndexed { i, _ -> i != idx } },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = oklch(0.65f, 0.14f, 25f), modifier = Modifier.size(14.dp))
                        }
                    }
                }

                PillButton(
                    text = ui.addStepBtn,
                    onClick = { draftSteps = draftSteps + DraftStep() },
                    textColor = oklch(0.70f, 0.02f, 70f), background = Color.Transparent, borderColor = oklch(0.38f, 0.02f, 55f),
                    fontSize = 12.5.sp, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp), shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.wrapContentWidth(),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f))
                    PillButton(
                        text = ui.cancelLabel, onClick = { building = false },
                        textColor = oklch(0.70f, 0.02f, 70f), background = Color.Transparent, borderColor = oklch(0.36f, 0.02f, 55f),
                        fontSize = 13.sp,
                    )
                    GradientPillButton(
                        text = ui.saveAttackBtn,
                        onClick = {
                            val name = draftName.trim()
                            val steps = draftSteps.filter { it.text.isNotBlank() }.mapIndexed { i, s ->
                                idCounter++
                                AttackStep(id = "formAtkStep${i}_$idCounter", type = s.type, text = s.text.trim())
                            }
                            if (name.isNotEmpty() && steps.isNotEmpty()) {
                                idCounter++
                                val attackId = editingAttackId ?: "formAtk$idCounter"
                                val newAttack = Attack(id = attackId, name = name, cost = draftCost, steps = steps)
                                val editing = editingAttackId
                                onChange(
                                    if (editing != null) attacks.map { if (it.id == editing) newAttack else it }
                                    else attacks + newAttack,
                                )
                                building = false
                            }
                        },
                        fontSize = 13.sp,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AttackSummaryRow(attack: Attack, onEdit: () -> Unit, onDelete: () -> Unit) {
    val meta = costMeta(attack.cost, LocalLanguage.current)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(10.dp))
            .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(10.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(attack.name, color = oklch(0.90f, 0.02f, 80f), fontFamily = Fonts.body, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Box(
                    modifier = Modifier
                        .background(meta.bg, RoundedCornerShape(999.dp))
                        .border(BorderStroke(1.dp, meta.border), RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(meta.label.uppercase(), color = meta.color, fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 9.5.sp)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                SmallSquareIconButton(Icons.Default.Edit, danger = false, onClick = onEdit)
                SmallSquareIconButton(Icons.Default.Close, danger = true, onClick = onDelete)
            }
        }
        Text(
            attack.steps.joinToString("  ·  ") { it.text }, color = oklch(0.65f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 12.sp,
            maxLines = 2, modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun SmallSquareIconButton(icon: ImageVector, danger: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .background(oklch(0.21f, 0.02f, 55f), RoundedCornerShape(7.dp))
            .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(7.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = if (danger) oklch(0.65f, 0.14f, 25f) else oklch(0.75f, 0.02f, 70f), modifier = Modifier.size(13.dp))
    }
}
