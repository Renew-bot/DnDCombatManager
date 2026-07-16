package com.example.dndcombatmanager.combat.i18n

import com.example.dndcombatmanager.combat.model.CONDITION_DESCRIPTIONS

/**
 * Conditions are stored and matched throughout the app by their French label (see [com.example.dndcombatmanager.combat.model.CONDITIONS]),
 * which acts as a stable internal id so existing combat state and saved presets keep working regardless of
 * the display language. This file only supplies the English translation layered on top for display purposes.
 */
val CONDITION_LABELS_EN: Map<String, String> = mapOf(
    "Aveuglé" to "Blinded",
    "Charmé" to "Charmed",
    "Effrayé" to "Frightened",
    "Empoisonné" to "Poisoned",
    "Entravé" to "Restrained",
    "Étourdi" to "Stunned",
    "Inconscient" to "Unconscious",
    "Invisible" to "Invisible",
    "Neutralisé" to "Incapacitated",
    "Paralysé" to "Paralyzed",
    "Pétrifié" to "Petrified",
    "À terre" to "Prone",
)

/** Descriptions from the D&D 5e SRD 5.1 (CC-BY-4.0). */
val CONDITION_DESCRIPTIONS_EN: Map<String, List<String>> = mapOf(
    "Aveuglé" to listOf(
        "A blinded creature can't see and automatically fails any ability check that requires sight.",
        "Attack rolls against the creature have advantage, and the creature's attack rolls have disadvantage.",
    ),
    "Charmé" to listOf(
        "A charmed creature can't attack the charmer or target the charmer with harmful abilities or magical effects.",
        "The charmer has advantage on any ability check to interact socially with the creature.",
    ),
    "Effrayé" to listOf(
        "A frightened creature has disadvantage on ability checks and attack rolls while the source of its fear is within line of sight.",
        "The creature can't willingly move closer to the source of its fear.",
    ),
    "Empoisonné" to listOf(
        "A poisoned creature has disadvantage on attack rolls and ability checks.",
    ),
    "Entravé" to listOf(
        "A restrained creature's speed becomes 0, and it can't benefit from any bonus to its speed.",
        "Attack rolls against the creature have advantage, and the creature's attack rolls have disadvantage.",
        "The creature has disadvantage on Dexterity saving throws.",
    ),
    "Étourdi" to listOf(
        "A stunned creature is incapacitated, can't move, and can speak only falteringly.",
        "The creature automatically fails Strength and Dexterity saving throws.",
        "Attack rolls against the creature have advantage.",
    ),
    "Inconscient" to listOf(
        "An unconscious creature is incapacitated, can't move or speak, and is unaware of its surroundings.",
        "The creature drops whatever it's holding and falls prone.",
        "The creature automatically fails Strength and Dexterity saving throws.",
        "Attack rolls against the creature have advantage.",
        "Any attack that hits the creature is a critical hit if the attacker is within 5 feet of the creature.",
    ),
    "Invisible" to listOf(
        "An invisible creature can't be seen without the aid of magic or a special sense. Its location can be detected by noise or tracks.",
        "Attack rolls against the creature have disadvantage, and the creature's attack rolls have advantage.",
    ),
    "Neutralisé" to listOf(
        "An incapacitated creature can't take actions or reactions.",
    ),
    "Paralysé" to listOf(
        "A paralyzed creature is incapacitated and can't move or speak.",
        "The creature automatically fails Strength and Dexterity saving throws.",
        "Attack rolls against the creature have advantage.",
        "Any attack that hits the creature is a critical hit if the attacker is within 5 feet of the creature.",
    ),
    "Pétrifié" to listOf(
        "A petrified creature is transformed, along with any nonmagical object it is wearing or carrying, into a solid inanimate substance. Its weight increases by a factor of ten, and it ceases aging.",
        "The creature is incapacitated, can't move or speak, and is unaware of its surroundings.",
        "Attack rolls against the creature have advantage.",
        "The creature automatically fails Strength and Dexterity saving throws.",
        "The creature has resistance to all damage.",
        "The creature is immune to poison and disease, although a poison or disease already in its system is suspended, not neutralized.",
    ),
    "À terre" to listOf(
        "A prone creature's only movement option is to crawl, unless it stands up, which ends the condition.",
        "The creature has disadvantage on attack rolls.",
        "An attack roll against the creature has advantage if the attacker is within 5 feet of the creature. Otherwise, the attack roll has disadvantage.",
    ),
)

fun conditionLabel(id: String, lang: Language): String =
    if (lang == Language.EN) CONDITION_LABELS_EN[id] ?: id else id

fun conditionDescription(id: String, lang: Language): List<String> =
    if (lang == Language.EN) CONDITION_DESCRIPTIONS_EN[id].orEmpty() else CONDITION_DESCRIPTIONS[id].orEmpty()
