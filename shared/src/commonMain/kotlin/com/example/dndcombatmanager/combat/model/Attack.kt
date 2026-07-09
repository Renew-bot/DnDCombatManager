package com.example.dndcombatmanager.combat.model

import kotlinx.serialization.Serializable

@Serializable
enum class AttackCost(val label: String) {
    ACTION("Action"),
    BONUS("Action bonus"),
    REACTION("Réaction"),
    LEGENDARY("Action légendaire"),
}

@Serializable
enum class StepType(val label: String) {
    ATTACK("Jet d'attaque"),
    DAMAGE("Dégâts"),
    OTHER("Autre"),
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

fun stepPlaceholder(type: StepType): String = when (type) {
    StepType.ATTACK -> "Ex. +7 pour toucher, portée 1,50 m"
    StepType.DAMAGE -> "Ex. 2d6+1d8+3 tranchants"
    StepType.OTHER -> "Ex. DD 18 Force ou agrippé"
}

/** [isOwnTurn] gates legendary actions, which D&D rules only let a creature spend on someone else's turn. */
fun Character.attackAvailability(cost: AttackCost, isOwnTurn: Boolean): Boolean = when (cost) {
    AttackCost.ACTION -> action
    AttackCost.BONUS -> bonus
    AttackCost.REACTION -> reaction
    AttackCost.LEGENDARY -> legendaryCurrent > 0 && !isOwnTurn
}
