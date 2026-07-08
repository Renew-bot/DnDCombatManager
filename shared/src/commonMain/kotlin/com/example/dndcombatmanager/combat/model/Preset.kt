package com.example.dndcombatmanager.combat.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterPreset(
    val id: String,
    val name: String,
    val type: CharacterType,
    val initiative: Int,
    val maxHp: Int,
    val ac: Int,
    val speed: Int,
    val speedFly: Int = 0,
    val speedSwim: Int = 0,
    val speedClimb: Int = 0,
    val saves: Saves = Saves(),
    val legendaryMax: Int = 0,
    val legendaryResMax: Int = 0,
    val attacks: List<Attack> = emptyList(),
)

fun CharacterPreset.metaLabel(): String =
    "${type.shortLabel()} · PV $maxHp · CA $ac · ${attacks.size} attaque(s)"

data class CharacterFormData(
    val name: String = "",
    val type: CharacterType = CharacterType.PJ,
    val initiative: Int = 10,
    val maxHp: Int = 10,
    val ac: Int = 10,
    val speed: Int = 9,
    val speedFly: Int = 0,
    val speedSwim: Int = 0,
    val speedClimb: Int = 0,
    val saves: Saves = Saves(),
    val legendaryMax: Int = 0,
    val legendaryResMax: Int = 0,
    val attacks: List<Attack> = emptyList(),
    val saveAsPreset: Boolean = false,
)
