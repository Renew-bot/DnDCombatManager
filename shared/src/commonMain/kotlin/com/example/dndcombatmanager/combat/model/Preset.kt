package com.example.dndcombatmanager.combat.model

import com.example.dndcombatmanager.combat.i18n.Language
import com.example.dndcombatmanager.combat.i18n.strings
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
    val stats: Saves = DEFAULT_STATS,
    val saves: Saves = Saves(),
    val legendaryMax: Int = 0,
    val legendaryResMax: Int = 0,
    val attacks: List<Attack> = emptyList(),
    val portrait: String? = null,
)

fun CharacterPreset.metaLabel(lang: Language): String =
    lang.strings().characterPresetMeta(type.shortLabel(lang), maxHp, ac, attacks.size)

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
    val stats: Saves = DEFAULT_STATS,
    val saveOverrides: SaveOverrides = SaveOverrides(),
    val legendaryMax: Int = 0,
    val legendaryResMax: Int = 0,
    val attacks: List<Attack> = emptyList(),
    val saveAsPreset: Boolean = false,
    val portrait: String? = null,
)
