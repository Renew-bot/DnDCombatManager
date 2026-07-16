package com.example.dndcombatmanager.combat.model

import com.example.dndcombatmanager.combat.i18n.Language
import com.example.dndcombatmanager.combat.i18n.strings
import kotlinx.serialization.Serializable

@Serializable
enum class AttackCost { ACTION, BONUS, REACTION, LEGENDARY }

fun AttackCost.label(lang: Language): String {
    val s = lang.strings()
    return when (this) {
        AttackCost.ACTION -> s.costAction
        AttackCost.BONUS -> s.costBonus
        AttackCost.REACTION -> s.costReaction
        AttackCost.LEGENDARY -> s.costLegendary
    }
}

@Serializable
enum class StepType { ATTACK, DAMAGE, OTHER }

fun StepType.label(lang: Language): String {
    val s = lang.strings()
    return when (this) {
        StepType.ATTACK -> s.stepAttack
        StepType.DAMAGE -> s.stepDamage
        StepType.OTHER -> s.stepOther
    }
}

@Serializable
data class AttackStep(
    val id: String,
    val type: StepType,
    val text: String,
)

@Serializable
data class Attack(
    val id: String,
    val name: String,
    val cost: AttackCost,
    val steps: List<AttackStep>,
)

fun stepPlaceholder(type: StepType, lang: Language): String {
    val s = lang.strings()
    return when (type) {
        StepType.ATTACK -> s.stepPlaceholderAttack
        StepType.DAMAGE -> s.stepPlaceholderDamage
        StepType.OTHER -> s.stepPlaceholderOther
    }
}

/** [isOwnTurn] gates legendary actions, which D&D rules only let a creature spend on someone else's turn. */
fun Character.attackAvailability(cost: AttackCost, isOwnTurn: Boolean): Boolean = when (cost) {
    AttackCost.ACTION -> action
    AttackCost.BONUS -> bonus
    AttackCost.REACTION -> reaction
    AttackCost.LEGENDARY -> legendaryCurrent > 0 && !isOwnTurn
}
