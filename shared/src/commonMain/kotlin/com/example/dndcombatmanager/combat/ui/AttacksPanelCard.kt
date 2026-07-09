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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatmanager.combat.model.Attack
import com.example.dndcombatmanager.combat.model.AttackCost
import com.example.dndcombatmanager.combat.model.AttackStep
import com.example.dndcombatmanager.combat.model.Character
import com.example.dndcombatmanager.combat.model.StepType
import com.example.dndcombatmanager.combat.model.attackAvailability
import com.example.dndcombatmanager.combat.model.stepPlaceholder
import com.example.dndcombatmanager.combat.state.RollResult
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

internal data class Meta(val color: Color, val bg: Color, val border: Color, val label: String)

internal fun stepMeta(type: StepType): Meta = when (type) {
    StepType.ATTACK -> Meta(oklch(0.80f, 0.13f, 70f), oklch(0.32f, 0.07f, 70f, 0.35f), oklch(0.55f, 0.11f, 70f, 0.7f), type.label)
    StepType.DAMAGE -> Meta(oklch(0.78f, 0.13f, 25f), oklch(0.30f, 0.09f, 25f, 0.35f), oklch(0.52f, 0.13f, 25f, 0.7f), type.label)
    StepType.OTHER -> Meta(oklch(0.78f, 0.06f, 220f), oklch(0.28f, 0.04f, 220f, 0.35f), oklch(0.50f, 0.07f, 220f, 0.7f), type.label)
}

internal fun costMeta(cost: AttackCost): Meta = when (cost) {
    AttackCost.ACTION -> Meta(oklch(0.80f, 0.13f, 70f), oklch(0.32f, 0.07f, 70f, 0.35f), oklch(0.55f, 0.11f, 70f, 0.7f), cost.label)
    AttackCost.BONUS -> Meta(oklch(0.78f, 0.1f, 195f), oklch(0.30f, 0.06f, 195f, 0.35f), oklch(0.52f, 0.09f, 195f, 0.7f), cost.label)
    AttackCost.REACTION -> Meta(oklch(0.78f, 0.1f, 300f), oklch(0.30f, 0.07f, 300f, 0.35f), oklch(0.52f, 0.1f, 300f, 0.7f), cost.label)
    AttackCost.LEGENDARY -> Meta(oklch(0.78f, 0.13f, 25f), oklch(0.30f, 0.09f, 25f, 0.35f), oklch(0.52f, 0.13f, 25f, 0.7f), cost.label)
}

internal data class DraftStep(val type: StepType = StepType.ATTACK, val text: String = "")

@Composable
fun AttacksPanelCard(
    character: Character,
    rolls: Map<String, Map<String, RollResult>>,
    targetingActive: Boolean,
    pendingAttackId: String?,
    isOwnTurn: Boolean,
    onSave: (String, Attack) -> Unit,
    onDelete: (String, String) -> Unit,
    onUse: (String, Attack) -> Unit,
    onRollWithoutTarget: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var building by remember(character.id) { mutableStateOf(false) }
    var editingAttackId by remember(character.id) { mutableStateOf<String?>(null) }
    var draftName by remember(character.id) { mutableStateOf("") }
    var draftCost by remember(character.id) { mutableStateOf(AttackCost.ACTION) }
    var draftSteps by remember(character.id) { mutableStateOf(listOf(DraftStep())) }
    var stepIdCounter by remember(character.id) { mutableStateOf(0) }

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

    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(listOf(oklch(0.23f, 0.025f, 55f), oklch(0.19f, 0.02f, 55f))),
                RoundedCornerShape(16.dp),
            )
            .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Attaques", color = oklch(0.90f, 0.03f, 75f), fontFamily = Fonts.display, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            if (!building) {
                PillButton(
                    text = "+ Attaque", onClick = { startAdd() },
                    textColor = oklch(0.85f, 0.1f, 70f), background = oklch(0.30f, 0.06f, 70f, 0.35f),
                    borderColor = oklch(0.55f, 0.1f, 70f, 0.6f), fontSize = 12.5.sp,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), shape = RoundedCornerShape(8.dp),
                )
            }
        }

        Box(modifier = Modifier.height(16.dp))

        if (!building) {
            if (character.attacks.isEmpty()) {
                Text(
                    "Aucune attaque enregistrée.", color = oklch(0.50f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 10.dp),
                    textAlign = TextAlign.Center,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    character.attacks.forEach { attack ->
                        val isPendingThis = pendingAttackId == attack.id
                        AttackCard(
                            attack = attack,
                            available = (character.attackAvailability(attack.cost, isOwnTurn) && !targetingActive) || isPendingThis,
                            pendingNoTarget = isPendingThis,
                            rolled = rolls[attack.id] ?: emptyMap(),
                            onEdit = { startEdit(attack) },
                            onDelete = { onDelete(character.id, attack.id) },
                            onUse = { if (isPendingThis) onRollWithoutTarget() else onUse(character.id, attack) },
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    FieldLabel("Nom de l'attaque", modifier = Modifier.weight(2f)) {
                        DarkTextField(value = draftName, onValueChange = { draftName = it }, placeholder = "Ex. Morsure")
                    }
                    FieldLabel("Coûte", modifier = Modifier.weight(1f)) {
                        DarkSelectField(
                            selected = draftCost,
                            options = AttackCost.entries.map { SelectOption(it, it.label) },
                            onSelect = { draftCost = it },
                        )
                    }
                }

                draftSteps.forEachIndexed { idx, row ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        DarkSelectField(
                            selected = row.type,
                            options = StepType.entries.map { SelectOption(it, it.label) },
                            onSelect = { newType -> draftSteps = draftSteps.mapIndexed { i, s -> if (i == idx) s.copy(type = newType) else s } },
                            modifier = Modifier.width(128.dp),
                        )
                        DarkTextField(
                            value = row.text,
                            onValueChange = { text -> draftSteps = draftSteps.mapIndexed { i, s -> if (i == idx) s.copy(text = text) else s } },
                            placeholder = stepPlaceholder(row.type),
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
                    text = "+ Ajouter une étape",
                    onClick = { draftSteps = draftSteps + DraftStep() },
                    textColor = oklch(0.70f, 0.02f, 70f), background = Color.Transparent, borderColor = oklch(0.38f, 0.02f, 55f),
                    fontSize = 12.5.sp, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp), shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.wrapContentWidth(),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f))
                    PillButton(
                        text = "Annuler", onClick = { building = false },
                        textColor = oklch(0.70f, 0.02f, 70f), background = Color.Transparent, borderColor = oklch(0.36f, 0.02f, 55f),
                        fontSize = 13.sp,
                    )
                    GradientPillButton(
                        text = "Enregistrer",
                        onClick = {
                            val name = draftName.trim()
                            val steps = draftSteps.filter { it.text.isNotBlank() }.mapIndexed { i, s ->
                                stepIdCounter++
                                AttackStep(id = "s${i}_$stepIdCounter", type = s.type, text = s.text.trim())
                            }
                            if (name.isNotEmpty() && steps.isNotEmpty()) {
                                val attackId = editingAttackId ?: "atk${character.id}_${stepIdCounter + 1}"
                                onSave(character.id, Attack(id = attackId, name = name, cost = draftCost, steps = steps))
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
private fun AttackCard(
    attack: Attack,
    available: Boolean,
    pendingNoTarget: Boolean,
    rolled: Map<String, RollResult>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUse: () -> Unit,
) {
    val meta = costMeta(attack.cost)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(11.dp))
            .border(BorderStroke(1.dp, oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(11.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(attack.name, color = oklch(0.90f, 0.02f, 80f), fontFamily = Fonts.body, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                SmallIconButton(Icons.Default.Edit, danger = false, onClick = onEdit)
                SmallIconButton(Icons.Default.Close, danger = true, onClick = onDelete)
            }
        }
        Box(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .background(meta.bg, RoundedCornerShape(999.dp))
                .border(BorderStroke(1.dp, meta.border), RoundedCornerShape(999.dp))
                .padding(horizontal = 9.dp, vertical = 3.dp),
        ) {
            Text(meta.label.uppercase(), color = meta.color, fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
        }
        Box(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            attack.steps.forEach { step ->
                val sMeta = stepMeta(step.type)
                val roll = rolled[step.id]
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(sMeta.bg, RoundedCornerShape(999.dp))
                            .border(BorderStroke(1.dp, sMeta.border), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(sMeta.label.uppercase(), color = sMeta.color, fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(step.text, color = oklch(0.80f, 0.02f, 80f), fontFamily = Fonts.body, fontSize = 13.sp, lineHeight = 18.sp)
                        if (roll != null) {
                            val rollColor = when (roll.crit) {
                                "success" -> oklch(0.70f, 0.16f, 145f)
                                "fail" -> oklch(0.65f, 0.18f, 25f)
                                else -> oklch(0.80f, 0.15f, 70f)
                            }
                            Text(roll.text, color = rollColor, fontFamily = Fonts.mono, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (available) oklch(0.34f, 0.09f, 70f, 0.5f) else oklch(0.21f, 0.02f, 55f), RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, if (available) oklch(0.55f, 0.11f, 70f, 0.8f) else oklch(0.30f, 0.02f, 55f)), RoundedCornerShape(8.dp))
                .clickable(enabled = available) { onUse() }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                when {
                    pendingNoTarget -> "Lancer sans cible"
                    available -> "Utiliser"
                    else -> "Indisponible"
                },
                color = if (available) oklch(0.90f, 0.1f, 75f) else oklch(0.45f, 0.02f, 70f),
                fontFamily = Fonts.body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
            )
        }
    }
}

@Composable
private fun SmallIconButton(icon: ImageVector, danger: Boolean, onClick: () -> Unit) {
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
