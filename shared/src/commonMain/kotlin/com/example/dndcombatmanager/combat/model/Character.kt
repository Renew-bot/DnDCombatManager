package com.example.dndcombatmanager.combat.model

import kotlinx.serialization.Serializable

@Serializable
enum class CharacterType { PJ, MONSTRE, BOSS }

@Serializable
data class Saves(
    val force: Int = 0,
    val dex: Int = 0,
    val con: Int = 0,
    val intel: Int = 0,
    val sagesse: Int = 0,
    val charisme: Int = 0,
) {
    fun get(key: SaveKey): Int = when (key) {
        SaveKey.FOR -> force
        SaveKey.DEX -> dex
        SaveKey.CON -> con
        SaveKey.INT -> intel
        SaveKey.SAG -> sagesse
        SaveKey.CHA -> charisme
    }

    fun with(key: SaveKey, value: Int): Saves = when (key) {
        SaveKey.FOR -> copy(force = value)
        SaveKey.DEX -> copy(dex = value)
        SaveKey.CON -> copy(con = value)
        SaveKey.INT -> copy(intel = value)
        SaveKey.SAG -> copy(sagesse = value)
        SaveKey.CHA -> copy(charisme = value)
    }
}

enum class SaveKey(val label: String) {
    FOR("FOR"), DEX("DEX"), CON("CON"), INT("INT"), SAG("SAG"), CHA("CHA")
}

data class Character(
    val id: String,
    val name: String,
    val type: CharacterType,
    val initiative: Int,
    val maxHp: Int,
    val currentHp: Int,
    val tempHp: Int = 0,
    val ac: Int,
    val speed: Int,
    val speedFly: Int = 0,
    val speedSwim: Int = 0,
    val speedClimb: Int = 0,
    val saves: Saves = Saves(),
    val action: Boolean = true,
    val bonus: Boolean = true,
    val reaction: Boolean = true,
    val legendaryMax: Int = 0,
    val legendaryCurrent: Int = 0,
    val legendaryResMax: Int = 0,
    val legendaryResCurrent: Int = 0,
    val conditions: List<String> = emptyList(),
    val exhaustion: Int = 0,
    val notes: String = "",
    val attacks: List<Attack> = emptyList(),
)

val CONDITIONS: List<String> = listOf(
    "Aveuglé", "Charmé", "Effrayé", "Empoisonné", "Entravé", "Étourdi",
    "Inconscient", "Invisible", "Neutralisé", "Paralysé", "Pétrifié", "À terre",
)

fun CharacterType.label(): String = when (this) {
    CharacterType.PJ -> "Personnage joueur"
    CharacterType.MONSTRE -> "Monstre"
    CharacterType.BOSS -> "Créature légendaire"
}

fun CharacterType.shortLabel(): String = when (this) {
    CharacterType.PJ -> "Joueur"
    CharacterType.MONSTRE -> "Monstre"
    CharacterType.BOSS -> "Créature légendaire"
}

fun healthPct(currentHp: Int, maxHp: Int): Float =
    if (maxHp <= 0) 0f else (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)

fun formatMod(n: Int): String = if (n >= 0) "+$n" else n.toString()

fun speedText(c: Character): String {
    val parts = mutableListOf("Marche ${c.speed}m")
    if (c.speedFly != 0) parts += "Vol ${c.speedFly}m"
    if (c.speedSwim != 0) parts += "Nage ${c.speedSwim}m"
    if (c.speedClimb != 0) parts += "Escalade ${c.speedClimb}m"
    return parts.joinToString(" · ")
}
