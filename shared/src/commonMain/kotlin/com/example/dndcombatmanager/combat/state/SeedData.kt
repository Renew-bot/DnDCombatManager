package com.example.dndcombatmanager.combat.state

import com.example.dndcombatmanager.combat.model.Attack
import com.example.dndcombatmanager.combat.model.AttackCost
import com.example.dndcombatmanager.combat.model.AttackStep
import com.example.dndcombatmanager.combat.model.Character
import com.example.dndcombatmanager.combat.model.CharacterType
import com.example.dndcombatmanager.combat.model.Saves
import com.example.dndcombatmanager.combat.model.StepType

/** Same starting encounter as the original design, so a fresh tracker isn't empty. */
fun seedCharacters(): List<Character> = listOf(
    Character(
        id = "c1", name = "Aeliana", type = CharacterType.PJ, initiative = 18,
        maxHp = 27, currentHp = 27, ac = 16, speed = 9,
        saves = Saves(force = 1, dex = 3, con = 2, intel = 0, sagesse = 1, charisme = 2),
        attacks = listOf(
            Attack(
                id = "a1", name = "Épée longue", cost = AttackCost.ACTION,
                steps = listOf(
                    AttackStep("a1s1", StepType.ATTACK, "+6 pour toucher, portée 1,50 m"),
                    AttackStep("a1s2", StepType.DAMAGE, "1d8+4 tranchants"),
                ),
            ),
        ),
    ),
    Character(
        id = "c2", name = "Borin Pierrefeu", type = CharacterType.PJ, initiative = 14,
        maxHp = 38, currentHp = 38, ac = 18, speed = 6,
        saves = Saves(force = 4, dex = 0, con = 3, intel = -1, sagesse = 1, charisme = 0),
    ),
    Character(
        id = "c3", name = "Gobelin éclaireur", type = CharacterType.MONSTRE, initiative = 12,
        maxHp = 7, currentHp = 7, ac = 13, speed = 9,
        saves = Saves(force = -1, dex = 2, con = 0, intel = -1, sagesse = 0, charisme = -1),
        attacks = listOf(
            Attack(
                id = "a2", name = "Cimeterre", cost = AttackCost.ACTION,
                steps = listOf(
                    AttackStep("a2s1", StepType.ATTACK, "+4 pour toucher, portée 1,50 m"),
                    AttackStep("a2s2", StepType.DAMAGE, "1d6+2 tranchants"),
                ),
            ),
        ),
    ),
    Character(
        id = "c4", name = "Dragon noir adulte", type = CharacterType.BOSS, initiative = 20,
        maxHp = 195, currentHp = 195, ac = 19, speed = 12, speedFly = 24, speedSwim = 12,
        saves = Saves(force = 7, dex = 3, con = 9, intel = 4, sagesse = 6, charisme = 8),
        legendaryMax = 3, legendaryCurrent = 3, legendaryResMax = 3, legendaryResCurrent = 3,
        attacks = listOf(
            Attack(
                id = "a3", name = "Morsure", cost = AttackCost.ACTION,
                steps = listOf(
                    AttackStep("a3s1", StepType.ATTACK, "+11 pour toucher, portée 3 m"),
                    AttackStep("a3s2", StepType.DAMAGE, "2d10+7 perforants"),
                    AttackStep("a3s3", StepType.OTHER, "Si la cible est une créature, elle est agrippée (évasion DD 18)"),
                ),
            ),
            Attack(
                id = "a4", name = "Griffe (légendaire)", cost = AttackCost.LEGENDARY,
                steps = listOf(
                    AttackStep("a4s1", StepType.ATTACK, "+11 pour toucher, portée 3 m (coût: 1 action légendaire)"),
                    AttackStep("a4s2", StepType.DAMAGE, "2d6+7 tranchants"),
                ),
            ),
        ),
    ),
)
