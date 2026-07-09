package com.example.dndcombatmanager.combat.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.dndcombatmanager.combat.model.Attack
import com.example.dndcombatmanager.combat.model.AttackCost
import com.example.dndcombatmanager.combat.model.AttackStep
import com.example.dndcombatmanager.combat.model.Character
import com.example.dndcombatmanager.combat.model.CharacterFormData
import com.example.dndcombatmanager.combat.model.CharacterPreset
import com.example.dndcombatmanager.combat.model.CombatPreset
import com.example.dndcombatmanager.combat.model.StepType
import com.example.dndcombatmanager.combat.model.attacksAgainstHaveAdvantage
import com.example.dndcombatmanager.combat.model.attacksAgainstHaveDisadvantage
import com.example.dndcombatmanager.combat.model.ownAttacksHaveAdvantage
import com.example.dndcombatmanager.combat.model.ownAttacksHaveDisadvantage
import com.example.dndcombatmanager.combat.model.resolveSaves
import com.example.dndcombatmanager.combat.model.toOverrides
import com.example.dndcombatmanager.combat.storage.PresetStorage
import com.example.dndcombatmanager.combat.storage.SavedPresets
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/** Result of a single rolled attack/damage step, with the numeric total used for AC comparisons. */
data class RollResult(val text: String, val crit: String?, val total: Int?)

/** An attack that's waiting for a target — nothing is rolled until one is picked. */
data class PendingAttack(
    val attackerId: String,
    val attackId: String,
    val attackName: String,
    val attackSteps: List<AttackStep>,
    val damageSteps: List<AttackStep>,
)

/** D&D advantage/disadvantage: roll 2d20 and keep the better/worse of the two. */
enum class RollMode { NORMAL, ADVANTAGE, DISADVANTAGE }

private fun rollAttackStep(text: String, mode: RollMode = RollMode.NORMAL): RollResult {
    val mod = Regex("""[+-]\s*\d+""").find(text)?.value?.replace(" ", "")?.toIntOrNull() ?: 0
    val (roll, rollLabel) = when (mode) {
        RollMode.NORMAL -> Random.nextInt(1, 21) to "d20"
        RollMode.ADVANTAGE -> {
            val a = Random.nextInt(1, 21); val b = Random.nextInt(1, 21)
            max(a, b) to "d20(avantage $a/$b)"
        }
        RollMode.DISADVANTAGE -> {
            val a = Random.nextInt(1, 21); val b = Random.nextInt(1, 21)
            min(a, b) to "d20(désavantage $a/$b)"
        }
    }
    val total = roll + mod
    val modStr = if (mod == 0) "" else if (mod > 0) "+$mod" else "$mod"
    val crit = if (roll == 20) "success" else if (roll == 1) "fail" else null
    val critTxt = when (crit) {
        "success" -> " — Critique !"
        "fail" -> " — Échec critique"
        else -> ""
    }
    val modSuffix = if (modStr.isNotEmpty()) " $modStr" else ""
    return RollResult("$rollLabel$modStr → $roll$modSuffix = $total$critTxt", crit, total)
}

/**
 * Rolls a damage formula that may chain several dice groups and flat modifiers,
 * e.g. "2d6+1d8+3" or "1d8+2d6-1 tranchants" (trailing descriptive text is ignored).
 */
private fun rollDamageStep(text: String): RollResult? {
    val formulaRegex = Regex("""^\s*((?:[+-]?\s*\d+\s*(?:d\s*\d+)?\s*)+)""", RegexOption.IGNORE_CASE)
    val formula = formulaRegex.find(text)?.groupValues?.get(1)?.trim() ?: return null
    if (formula.isEmpty()) return null

    val termRegex = Regex("""([+-]?)\s*(\d+)(?:\s*d\s*(\d+))?""", RegexOption.IGNORE_CASE)
    val terms = termRegex.findAll(formula).toList()
    if (terms.isEmpty()) return null

    var total = 0
    var hasDice = false
    val displayParts = mutableListOf<String>()
    terms.forEachIndexed { idx, m ->
        val sign = if (m.groupValues[1] == "-") -1 else 1
        val numberStr = m.groupValues[2]
        val sidesStr = m.groupValues[3]
        val operator = when {
            idx == 0 && sign < 0 -> "-"
            idx == 0 -> ""
            sign < 0 -> " - "
            else -> " + "
        }
        if (sidesStr.isNotEmpty()) {
            val sides = sidesStr.toIntOrNull() ?: return@forEachIndexed
            val count = (numberStr.toIntOrNull() ?: 1).coerceAtLeast(1)
            hasDice = true
            val dice = List(count) { Random.nextInt(1, sides + 1) }
            total += sign * dice.sum()
            displayParts += "$operator[${dice.joinToString(", ")}]"
        } else {
            val value = numberStr.toIntOrNull() ?: 0
            total += sign * value
            displayParts += "$operator$value"
        }
    }
    if (!hasDice) return null
    return RollResult("$formula → ${displayParts.joinToString("")} = $total", null, total)
}

/** Holds the whole combat encounter and every mutation the UI can trigger. */
class CombatTrackerState {
    var characters by mutableStateOf(emptyList<Character>())
        private set
    var round by mutableStateOf(1)
        private set
    var activeId by mutableStateOf(characters.firstOrNull()?.id)
        private set
    var viewingId by mutableStateOf<String?>(null)
        private set
    var layout by mutableStateOf(Layout.SIDEBAR)
    var modalCharId by mutableStateOf<String?>(null)
        private set

    var attackRolls by mutableStateOf<Map<String, Map<String, Map<String, RollResult>>>>(emptyMap())
        private set
    var pendingAttack by mutableStateOf<PendingAttack?>(null)
        private set
    /** Target picked while [pendingAttack] is active, but they're prone so we need the attacker's range first. */
    var pendingProneTargetId by mutableStateOf<String?>(null)
        private set

    var showForm by mutableStateOf(false)
        private set
    var editingId by mutableStateOf<String?>(null)
        private set
    var formData by mutableStateOf(CharacterFormData())

    private val savedPresets = PresetStorage.load()

    private var _presets by mutableStateOf(savedPresets.presets)
    var presets: List<CharacterPreset>
        get() = _presets
        private set(value) {
            _presets = value
            persistPresets()
        }
    var showPresets by mutableStateOf(false)
        private set

    private var _combatPresets by mutableStateOf(savedPresets.combatPresets)
    var combatPresets: List<CombatPreset>
        get() = _combatPresets
        private set(value) {
            _combatPresets = value
            persistPresets()
        }
    var showCombatPresets by mutableStateOf(false)
        private set
    var showSaveCombatDialog by mutableStateOf(false)
        private set
    var saveCombatName by mutableStateOf("")
        private set

    // Confirmation dialogs (stand in for window.confirm, which has no KMP equivalent).
    var pendingDeleteId by mutableStateOf<String?>(null)
        private set
    var pendingDeletePresetId by mutableStateOf<String?>(null)
        private set
    var pendingDeleteCombatPresetId by mutableStateOf<String?>(null)
        private set
    var pendingLoadCombatPresetId by mutableStateOf<String?>(null)
        private set
    var pendingClearAll by mutableStateOf(false)
        private set

    private var idCounter = 0
    private fun nextId(prefix: String): String {
        idCounter++
        return "$prefix$idCounter"
    }

    private fun persistPresets() {
        PresetStorage.save(SavedPresets(presets = _presets, combatPresets = _combatPresets))
    }

    val sortedCharacters: List<Character>
        get() = characters.sortedWith(compareByDescending<Character> { it.initiative }.thenBy { it.name })

    val activeCharacter: Character?
        get() = characters.find { it.id == activeId }

    val effectiveViewingId: String?
        get() = viewingId ?: activeId

    val viewingCharacter: Character?
        get() = characters.find { it.id == effectiveViewingId } ?: sortedCharacters.firstOrNull()

    val modalCharacter: Character?
        get() = if (layout == Layout.FOCUS) characters.find { it.id == modalCharId } else null

    private fun updateChar(id: String, patch: (Character) -> Character) {
        characters = characters.map { if (it.id == id) patch(it) else it }
    }

    fun changeLayout(l: Layout) {
        layout = l
        modalCharId = null
    }

    fun selectViewing(id: String) {
        viewingId = id
    }

    fun jumpToActive() {
        viewingId = null
    }

    fun openModal(id: String) {
        if (id != activeId) modalCharId = id
    }

    fun closeModal() {
        modalCharId = null
    }

    fun nextTurn() {
        val sorted = sortedCharacters
        if (sorted.isEmpty()) return
        val idx = sorted.indexOfFirst { it.id == activeId }
        val nextIdx = if (idx == -1) 0 else (idx + 1) % sorted.size
        var newRound = round
        var newCharacters = characters
        if (nextIdx == 0) {
            newRound += 1
            newCharacters = newCharacters.map { it.copy(legendaryCurrent = it.legendaryMax) }
        }
        val nextChar = sorted[nextIdx]
        newCharacters = newCharacters.map {
            if (it.id == nextChar.id) it.copy(action = true, bonus = true, reaction = true) else it
        }
        characters = newCharacters
        round = newRound
        activeId = nextChar.id
        viewingId = null
        pendingAttack = null
        pendingProneTargetId = null
    }

    fun handleDamage(id: String, amount: Int) = updateChar(id) { c ->
        var temp = c.tempHp
        var dmg = amount
        val absorbed = min(temp, dmg)
        temp -= absorbed
        dmg -= absorbed
        c.copy(tempHp = temp, currentHp = max(0, c.currentHp - dmg))
    }

    fun handleHeal(id: String, amount: Int) = updateChar(id) { c ->
        c.copy(currentHp = min(c.maxHp, c.currentHp + amount))
    }

    fun handleTempHp(id: String, value: Int) = updateChar(id) { c -> c.copy(tempHp = max(0, value)) }

    fun handleToggleCondition(id: String, name: String) = updateChar(id) { c ->
        val has = c.conditions.contains(name)
        c.copy(
            conditions = if (has) c.conditions.filter { it != name } else c.conditions + name,
            charmedBy = if (name == "Charmé" && has) null else c.charmedBy,
        )
    }

    fun handleSetCharmedBy(id: String, charmerId: String?) = updateChar(id) { c -> c.copy(charmedBy = charmerId) }

    fun handleToggleResource(id: String, key: ResourceKey) = updateChar(id) { c ->
        when (key) {
            ResourceKey.ACTION -> c.copy(action = !c.action)
            ResourceKey.BONUS -> c.copy(bonus = !c.bonus)
            ResourceKey.REACTION -> c.copy(reaction = !c.reaction)
        }
    }

    fun handleLegendaryUse(id: String, delta: Int) = updateChar(id) { c ->
        c.copy(legendaryCurrent = max(0, min(c.legendaryMax, c.legendaryCurrent + delta)))
    }

    fun handleLegendaryResUse(id: String, delta: Int) = updateChar(id) { c ->
        c.copy(legendaryResCurrent = max(0, min(c.legendaryResMax, c.legendaryResCurrent + delta)))
    }

    fun handleSetExhaustion(id: String, level: Int) = updateChar(id) { c ->
        c.copy(exhaustion = max(0, min(6, level)))
    }

    fun handleNotes(id: String, text: String) = updateChar(id) { c -> c.copy(notes = text) }

    fun handleSetPortrait(id: String, portrait: String?) = updateChar(id) { c -> c.copy(portrait = portrait) }

    fun handleSaveAttack(charId: String, attack: Attack) = updateChar(charId) { c ->
        val idx = c.attacks.indexOfFirst { it.id == attack.id }
        val nextAttacks = if (idx == -1) c.attacks + attack else c.attacks.mapIndexed { i, a -> if (i == idx) attack else a }
        c.copy(attacks = nextAttacks)
    }

    fun handleDeleteAttack(charId: String, attackId: String) = updateChar(charId) { c ->
        c.copy(attacks = c.attacks.filter { it.id != attackId })
    }

    fun handleUseAttack(charId: String, cost: AttackCost) = updateChar(charId) { c ->
        when (cost) {
            AttackCost.ACTION -> if (c.action) c.copy(action = false) else c
            AttackCost.BONUS -> if (c.bonus) c.copy(bonus = false) else c
            AttackCost.REACTION -> if (c.reaction) c.copy(reaction = false) else c
            AttackCost.LEGENDARY -> if (c.legendaryCurrent > 0) c.copy(legendaryCurrent = c.legendaryCurrent - 1) else c
        }
    }

    fun rollsFor(charId: String): Map<String, Map<String, RollResult>> = attackRolls[charId] ?: emptyMap()

    private fun storeRolls(charId: String, attackId: String, results: Map<String, RollResult>) {
        val charRolls = attackRolls[charId] ?: emptyMap()
        val existing = charRolls[attackId] ?: emptyMap()
        attackRolls = attackRolls + (charId to (charRolls + (attackId to (existing + results))))
    }

    /** Consumes the attacker's action economy and opens targeting — nothing is rolled until a target is picked. */
    fun beginAttack(charId: String, attack: Attack) {
        if (pendingAttack != null) return
        handleUseAttack(charId, attack.cost)
        pendingAttack = PendingAttack(
            attackerId = charId, attackId = attack.id, attackName = attack.name,
            attackSteps = attack.steps.filter { it.type == StepType.ATTACK },
            damageSteps = attack.steps.filter { it.type == StepType.DAMAGE },
        )
    }

    /**
     * Advantage/disadvantage from the attacker's own conditions and, if there's a target, theirs — they cancel out
     * if both apply. A prone target's effect on attacks against it depends on range, so [proneRangeMeters] (only
     * asked for when the target is prone) supplies that: <= 1.5m is melee reach (advantage), further is disadvantage.
     */
    private fun rollModeFor(attackerId: String, targetId: String?, proneRangeMeters: Double? = null): RollMode {
        val attacker = characters.find { it.id == attackerId }
        val target = targetId?.let { id -> characters.find { it.id == id } }
        var advantage = (attacker?.ownAttacksHaveAdvantage() == true) || (target?.attacksAgainstHaveAdvantage() == true)
        var disadvantage = (attacker?.ownAttacksHaveDisadvantage() == true) || (target?.attacksAgainstHaveDisadvantage() == true)
        if (target != null && target.conditions.contains("À terre") && proneRangeMeters != null) {
            if (proneRangeMeters <= 1.5) advantage = true else disadvantage = true
        }
        return when {
            advantage && disadvantage -> RollMode.NORMAL
            advantage -> RollMode.ADVANTAGE
            disadvantage -> RollMode.DISADVANTAGE
            else -> RollMode.NORMAL
        }
    }

    /** Sidebar/timeline/focus target click: if the target is prone, asks for range first via [pendingProneTargetId]. */
    private fun beginResolveAttack(targetId: String) {
        val pending = pendingAttack ?: return
        val attacker = characters.find { it.id == pending.attackerId }
        if (attacker?.charmedBy == targetId) return // a charmed character can't target their charmer
        val target = characters.find { it.id == targetId } ?: return
        if (target.conditions.contains("À terre")) {
            pendingProneTargetId = targetId
        } else {
            resolveAttackTarget(targetId)
        }
    }

    /** Resolves the prone-range prompt and rolls the attack against the chosen target. */
    fun confirmProneDistance(meters: Double) {
        val targetId = pendingProneTargetId ?: return
        pendingProneTargetId = null
        resolveAttackTarget(targetId, proneRangeMeters = meters)
    }

    fun cancelProneDistance() {
        pendingProneTargetId = null
    }

    /** Rolls the pending attack against the chosen target's AC (roll >= AC hits) and applies damage on a hit. */
    fun resolveAttackTarget(targetId: String, proneRangeMeters: Double? = null) {
        val pending = pendingAttack ?: return
        val target = characters.find { it.id == targetId } ?: return
        val mode = rollModeFor(pending.attackerId, targetId, proneRangeMeters)
        val results = mutableMapOf<String, RollResult>()
        var bestTotal: Int? = null
        pending.attackSteps.forEach { step ->
            val r = rollAttackStep(step.text, mode)
            val suffix = r.total?.let { t -> if (t >= target.ac) " — Touché (CA ${target.ac})" else " — Raté (CA ${target.ac})" } ?: ""
            results[step.id] = r.copy(text = r.text + suffix)
            r.total?.let { t -> bestTotal = if (bestTotal == null) t else max(bestTotal!!, t) }
        }
        val hits = bestTotal == null || bestTotal >= target.ac
        if (hits) {
            var totalDamage = 0
            pending.damageSteps.forEach { step ->
                rollDamageStep(step.text)?.let { r -> results[step.id] = r; totalDamage += r.total ?: 0 }
            }
            if (totalDamage > 0) handleDamage(targetId, totalDamage)
        }
        storeRolls(pending.attackerId, pending.attackId, results)
        pendingAttack = null
    }

    /** Rolls the pending attack's steps with no target picked — no AC check, no damage applied. */
    fun resolveAttackWithoutTarget() {
        val pending = pendingAttack ?: return
        val mode = rollModeFor(pending.attackerId, null)
        val results = mutableMapOf<String, RollResult>()
        pending.attackSteps.forEach { step -> results[step.id] = rollAttackStep(step.text, mode) }
        pending.damageSteps.forEach { step -> rollDamageStep(step.text)?.let { results[step.id] = it } }
        storeRolls(pending.attackerId, pending.attackId, results)
        pendingAttack = null
    }

    fun cancelAttack() {
        pendingAttack = null
        pendingProneTargetId = null
    }

    /** Sidebar/timeline card click: resolves the pending attack against the clicked target, or just switches the view. */
    fun handleCharacterClick(id: String) {
        val pending = pendingAttack
        if (pending != null) {
            if (id == pending.attackerId) cancelAttack() else beginResolveAttack(id)
            return
        }
        selectViewing(id)
    }

    /** Focus tile click: same targeting short-circuit as [handleCharacterClick], otherwise opens the modal. */
    fun handleFocusTileClick(id: String) {
        val pending = pendingAttack
        if (pending != null && id != pending.attackerId) {
            beginResolveAttack(id)
            return
        }
        openModal(id)
    }

    fun requestDelete(id: String) {
        pendingDeleteId = id
    }

    fun cancelDelete() {
        pendingDeleteId = null
    }

    fun confirmDelete() {
        val id = pendingDeleteId ?: return
        val remaining = characters.filter { it.id != id }.map { if (it.charmedBy == id) it.copy(charmedBy = null) else it }
        characters = remaining
        if (activeId == id) {
            val sorted = remaining.sortedByDescending { it.initiative }
            activeId = sorted.firstOrNull()?.id
        }
        if (viewingId == id) viewingId = null
        if (modalCharId == id) modalCharId = null
        if (pendingAttack?.attackerId == id) pendingAttack = null
        if (pendingProneTargetId == id) pendingProneTargetId = null
        pendingDeleteId = null
    }

    fun requestClearAll() {
        pendingClearAll = true
    }

    fun cancelClearAll() {
        pendingClearAll = false
    }

    fun confirmClearAll() {
        characters = emptyList()
        round = 1
        activeId = null
        viewingId = null
        modalCharId = null
        pendingAttack = null
        pendingProneTargetId = null
        attackRolls = emptyMap()
        pendingClearAll = false
    }

    private fun buildPresetFrom(id: String, c: Character): CharacterPreset = CharacterPreset(
        id = id, name = c.name, type = c.type, initiative = c.initiative, maxHp = c.maxHp, ac = c.ac,
        speed = c.speed, speedFly = c.speedFly, speedSwim = c.speedSwim, speedClimb = c.speedClimb,
        stats = c.stats, saves = c.saves, legendaryMax = c.legendaryMax, legendaryResMax = c.legendaryResMax,
        attacks = c.attacks, portrait = c.portrait,
    )

    private fun buildPresetFrom(id: String, fd: CharacterFormData): CharacterPreset = CharacterPreset(
        id = id, name = fd.name, type = fd.type, initiative = fd.initiative, maxHp = fd.maxHp, ac = fd.ac,
        speed = fd.speed, speedFly = fd.speedFly, speedSwim = fd.speedSwim, speedClimb = fd.speedClimb,
        stats = fd.stats, saves = resolveSaves(fd.stats, fd.saveOverrides),
        legendaryMax = fd.legendaryMax, legendaryResMax = fd.legendaryResMax,
        attacks = fd.attacks, portrait = fd.portrait,
    )

    fun handleSavePreset(character: Character) {
        presets = presets + buildPresetFrom(nextId("preset"), character)
    }

    fun requestDeletePreset(id: String) {
        pendingDeletePresetId = id
    }

    fun cancelDeletePreset() {
        pendingDeletePresetId = null
    }

    fun confirmDeletePreset() {
        val id = pendingDeletePresetId ?: return
        presets = presets.filter { it.id != id }
        pendingDeletePresetId = null
    }

    fun openPresets() {
        showPresets = true
    }

    fun closePresets() {
        showPresets = false
    }

    fun addFromPreset(preset: CharacterPreset) {
        val id = nextId("c")
        val newChar = Character(
            id = id, name = preset.name, type = preset.type, initiative = preset.initiative,
            maxHp = preset.maxHp, currentHp = preset.maxHp, tempHp = 0, ac = preset.ac, speed = preset.speed,
            speedFly = preset.speedFly, speedSwim = preset.speedSwim, speedClimb = preset.speedClimb,
            stats = preset.stats, saves = preset.saves, legendaryMax = preset.legendaryMax, legendaryCurrent = preset.legendaryMax,
            legendaryResMax = preset.legendaryResMax, legendaryResCurrent = preset.legendaryResMax,
            attacks = preset.attacks.mapIndexed { i, a -> a.copy(id = nextId("atk${i}_")) },
            portrait = preset.portrait,
        )
        characters = characters + newChar
        if (activeId == null) activeId = id
    }

    fun openCombatPresets() {
        showCombatPresets = true
    }

    fun closeCombatPresets() {
        showCombatPresets = false
    }

    fun openSaveCombatDialog() {
        showSaveCombatDialog = true
        saveCombatName = ""
    }

    fun closeSaveCombatDialog() {
        showSaveCombatDialog = false
    }

    fun changeSaveCombatName(name: String) {
        saveCombatName = name
    }

    fun confirmSaveCombat() {
        val name = saveCombatName.trim()
        if (name.isEmpty() || characters.isEmpty()) return
        val snapshot = characters.map { buildPresetFrom(nextId("cpc"), it) }
        combatPresets = combatPresets + CombatPreset(nextId("combat"), name, snapshot)
        showSaveCombatDialog = false
    }

    fun requestDeleteCombatPreset(id: String) {
        pendingDeleteCombatPresetId = id
    }

    fun cancelDeleteCombatPreset() {
        pendingDeleteCombatPresetId = null
    }

    fun confirmDeleteCombatPreset() {
        val id = pendingDeleteCombatPresetId ?: return
        combatPresets = combatPresets.filter { it.id != id }
        pendingDeleteCombatPresetId = null
    }

    private fun materializeCombatPreset(preset: CombatPreset): List<Character> = preset.characters.map { cp ->
        Character(
            id = nextId("c"), name = cp.name, type = cp.type, initiative = cp.initiative,
            maxHp = cp.maxHp, currentHp = cp.maxHp, tempHp = 0, ac = cp.ac, speed = cp.speed,
            speedFly = cp.speedFly, speedSwim = cp.speedSwim, speedClimb = cp.speedClimb,
            stats = cp.stats, saves = cp.saves, legendaryMax = cp.legendaryMax, legendaryCurrent = cp.legendaryMax,
            legendaryResMax = cp.legendaryResMax, legendaryResCurrent = cp.legendaryResMax,
            attacks = cp.attacks.mapIndexed { i, a -> a.copy(id = nextId("atk${i}_")) },
            portrait = cp.portrait,
        )
    }

    private fun loadCombatPreset(preset: CombatPreset) {
        val newCharacters = materializeCombatPreset(preset)
        characters = newCharacters
        round = 1
        activeId = newCharacters.sortedByDescending { it.initiative }.firstOrNull()?.id
        viewingId = null
        modalCharId = null
        pendingAttack = null
        pendingProneTargetId = null
        attackRolls = emptyMap()
        showCombatPresets = false
    }

    /** Loading replaces the current roster, so confirm first if there's anything to lose. */
    fun requestLoadCombatPreset(id: String) {
        val preset = combatPresets.find { it.id == id } ?: return
        if (characters.isEmpty()) loadCombatPreset(preset) else pendingLoadCombatPresetId = id
    }

    fun cancelLoadCombatPreset() {
        pendingLoadCombatPresetId = null
    }

    fun confirmLoadCombatPreset() {
        val id = pendingLoadCombatPresetId ?: return
        pendingLoadCombatPresetId = null
        val preset = combatPresets.find { it.id == id } ?: return
        loadCombatPreset(preset)
    }

    fun openAddForm() {
        showForm = true
        editingId = null
        formData = CharacterFormData()
    }

    fun openEditForm(id: String) {
        val c = characters.find { it.id == id } ?: return
        showForm = true
        editingId = id
        modalCharId = null
        formData = CharacterFormData(
            name = c.name, type = c.type, initiative = c.initiative, maxHp = c.maxHp, ac = c.ac, speed = c.speed,
            speedFly = c.speedFly, speedSwim = c.speedSwim, speedClimb = c.speedClimb,
            stats = c.stats, saveOverrides = c.saves.toOverrides(),
            legendaryMax = c.legendaryMax, legendaryResMax = c.legendaryResMax, attacks = c.attacks, saveAsPreset = false,
            portrait = c.portrait,
        )
    }

    fun closeForm() {
        showForm = false
    }

    fun submitForm() {
        val fd = formData
        if (fd.name.isBlank()) return
        val editing = editingId
        val resolvedSaves = resolveSaves(fd.stats, fd.saveOverrides)
        if (editing != null) {
            characters = characters.map { c ->
                if (c.id != editing) return@map c
                c.copy(
                    name = fd.name, type = fd.type, initiative = fd.initiative, maxHp = fd.maxHp, ac = fd.ac,
                    speed = fd.speed, speedFly = fd.speedFly, speedSwim = fd.speedSwim, speedClimb = fd.speedClimb,
                    stats = fd.stats, saves = resolvedSaves, legendaryMax = fd.legendaryMax, legendaryResMax = fd.legendaryResMax,
                    currentHp = min(c.currentHp, fd.maxHp), legendaryCurrent = min(c.legendaryCurrent, fd.legendaryMax),
                    legendaryResCurrent = min(c.legendaryResCurrent, fd.legendaryResMax),
                    attacks = fd.attacks, portrait = fd.portrait,
                )
            }
            if (fd.saveAsPreset) presets = presets + buildPresetFrom(nextId("preset"), fd)
        } else {
            val id = nextId("c")
            val newChar = Character(
                id = id, name = fd.name, type = fd.type, initiative = fd.initiative, maxHp = fd.maxHp,
                currentHp = fd.maxHp, tempHp = 0, ac = fd.ac, speed = fd.speed, speedFly = fd.speedFly,
                speedSwim = fd.speedSwim, speedClimb = fd.speedClimb, stats = fd.stats, saves = resolvedSaves,
                legendaryMax = fd.legendaryMax, legendaryCurrent = fd.legendaryMax,
                legendaryResMax = fd.legendaryResMax, legendaryResCurrent = fd.legendaryResMax,
                attacks = fd.attacks, portrait = fd.portrait,
            )
            characters = characters + newChar
            if (activeId == null) activeId = id
            if (fd.saveAsPreset) presets = presets + buildPresetFrom(nextId("preset"), newChar)
        }
        showForm = false
    }
}
