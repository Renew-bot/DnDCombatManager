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
    /** Base64-encoded portrait image (PNG/JPEG/etc.), or null if none was set. */
    val portrait: String? = null,
)

val CONDITIONS: List<String> = listOf(
    "Aveuglé", "Charmé", "Effrayé", "Empoisonné", "Entravé", "Étourdi",
    "Inconscient", "Invisible", "Neutralisé", "Paralysé", "Pétrifié", "À terre",
)

/** Descriptions tirées de https://www.aidedd.org/dnd/etats.php */
val CONDITION_DESCRIPTIONS: Map<String, List<String>> = mapOf(
    "Aveuglé" to listOf(
        "Une créature aveuglée ne voit pas et rate automatiquement tout jet de caractéristique qui nécessite la vue.",
        "Les jets d'attaque contre la créature ont un avantage, et les jets d'attaque de la créature ont un désavantage.",
    ),
    "Charmé" to listOf(
        "Une créature charmée ne peut pas attaquer le charmeur ou le cibler avec des capacités ou des effets magiques nuisibles.",
        "Le charmeur a un avantage à ses jets de caractéristique pour interagir socialement avec la créature.",
    ),
    "Effrayé" to listOf(
        "Une créature effrayée a un désavantage aux jets de caractéristique et aux jets d'attaque tant que la source de sa peur est dans sa ligne de vue.",
        "La créature ne peut se rapprocher volontairement de la source de sa peur.",
    ),
    "Empoisonné" to listOf(
        "Une créature empoisonnée a un désavantage aux jets d'attaque et aux jets de caractéristique.",
    ),
    "Entravé" to listOf(
        "La vitesse d'une créature entravée passe à 0, et elle ne peut bénéficier d'aucun bonus à sa vitesse.",
        "Les jets d'attaque contre la créature ont un avantage, et les jets d'attaque de la créature ont un désavantage.",
        "La créature a un désavantage à ses jets de sauvegarde de Dextérité.",
    ),
    "Étourdi" to listOf(
        "Une créature étourdie est incapable d'agir, ne peut plus bouger et parle de manière hésitante.",
        "La créature rate automatiquement ses jets de sauvegarde de Force et de Dextérité.",
        "Les jets d'attaque contre la créature ont un avantage.",
    ),
    "Inconscient" to listOf(
        "Une créature inconsciente est incapable d'agir, ne peut plus bouger ni parler, et n'est plus consciente de ce qui se passe autour d'elle.",
        "La créature lâche ce qu'elle tenait et tombe à terre.",
        "La créature rate automatiquement ses jets de sauvegarde de Force et de Dextérité.",
        "Les jets d'attaque contre la créature ont un avantage.",
        "Toute attaque qui touche la créature est un coup critique si l'attaquant est à 1,50 mètre ou moins de la créature.",
    ),
    "Invisible" to listOf(
        "Une créature invisible ne peut être vue sans l'aide de la magie ou un sens particulier. L'emplacement peut être détecté par un bruit ou les traces laissées.",
        "Les jets d'attaque contre la créature ont un désavantage, et les jets d'attaque de la créature ont un avantage.",
    ),
    "Neutralisé" to listOf(
        "Une créature incapable d'agir ne peut effectuer aucune action ni aucune réaction.",
    ),
    "Paralysé" to listOf(
        "Une créature paralysée est incapable d'agir et ne peut plus bouger ni parler.",
        "La créature rate automatiquement ses jets de sauvegarde de Force et de Dextérité.",
        "Les jets d'attaque contre la créature ont un avantage.",
        "Toute attaque qui touche la créature est un coup critique si l'attaquant est à 1,50 mètre ou moins de la créature.",
    ),
    "Pétrifié" to listOf(
        "Une créature pétrifiée est transformée en substance inanimée solide. Son poids est multiplié par dix et son vieillissement cesse.",
        "La créature est incapable d'agir, ne peut plus bouger ni parler, et n'est plus consciente de ce qui se passe autour d'elle.",
        "Les jets d'attaque contre la créature ont un avantage.",
        "La créature rate automatiquement ses jets de sauvegarde de Force et de Dextérité.",
        "La créature obtient la résistance contre tous les types de dégâts.",
        "La créature est immunisée contre le poison et la maladie, mais un poison ou une maladie déjà dans son organisme est seulement suspendu, pas neutralisé.",
    ),
    "À terre" to listOf(
        "La seule option de mouvement possible est de ramper, à moins qu'elle ne se relève et mette alors un terme à son état.",
        "La créature a un désavantage aux jets d'attaque.",
        "Un jet d'attaque contre la créature a un avantage si l'attaquant est à 1,50 mètre ou moins de la créature. Sinon, le jet d'attaque a un désavantage.",
    ),
)

private val SPEED_ZERO_CONDITIONS = setOf("Entravé", "Étourdi", "Inconscient", "Paralysé", "Pétrifié")
private val ACTION_BLOCKING_CONDITIONS = setOf("Étourdi", "Inconscient", "Paralysé", "Pétrifié", "Neutralisé")
private val AUTO_FAIL_SAVE_CONDITIONS = setOf("Étourdi", "Inconscient", "Paralysé", "Pétrifié")
private val DEX_SAVE_DISADVANTAGE_CONDITIONS = setOf("Entravé")

/** États dont l'effet modifie une donnée affichée sur la fiche de personnage. */
val CONDITIONS_WITH_DATA_EFFECT: Set<String> = SPEED_ZERO_CONDITIONS + ACTION_BLOCKING_CONDITIONS

fun Character.speedForcedToZero(): Boolean = conditions.any { it in SPEED_ZERO_CONDITIONS }

fun Character.actionsBlockedByCondition(): Boolean = conditions.any { it in ACTION_BLOCKING_CONDITIONS }

fun Character.saveAutoFails(key: SaveKey): Boolean =
    (key == SaveKey.FOR || key == SaveKey.DEX) && conditions.any { it in AUTO_FAIL_SAVE_CONDITIONS }

fun Character.saveHasDisadvantage(key: SaveKey): Boolean =
    key == SaveKey.DEX && !saveAutoFails(key) && conditions.any { it in DEX_SAVE_DISADVANTAGE_CONDITIONS }

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
    if (c.speedForcedToZero()) return "0m"
    val parts = mutableListOf("Marche ${c.speed}m")
    if (c.speedFly != 0) parts += "Vol ${c.speedFly}m"
    if (c.speedSwim != 0) parts += "Nage ${c.speedSwim}m"
    if (c.speedClimb != 0) parts += "Escalade ${c.speedClimb}m"
    return parts.joinToString(" · ")
}
