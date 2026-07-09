package com.example.dndcombatmanager.combat.storage

import com.example.dndcombatmanager.combat.model.Character
import com.example.dndcombatmanager.combat.model.CharacterPreset
import com.example.dndcombatmanager.combat.model.CharacterType
import com.example.dndcombatmanager.combat.state.CombatTrackerState
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals

class PresetStorageJvmTest {

    private fun withIsolatedHome(block: () -> Unit) {
        val originalHome = System.getProperty("user.home")
        val tempHome = createTempDirectory("dndcombatmanager-test").toFile()
        System.setProperty("user.home", tempHome.absolutePath)
        try {
            block()
        } finally {
            System.setProperty("user.home", originalHome)
            tempHome.deleteRecursively()
        }
    }

    // Both scenarios share one isolated home directory: PresetStorage caches its target
    // file lazily for the whole JVM process, so splitting this across two @Test methods
    // would make the second one silently reuse (and miss) the first one's temp dir.
    @Test
    fun presetsPersistAcrossRestartsAndRawStorageRoundTrips() = withIsolatedHome {
        val first = CombatTrackerState()
        val monster = Character(
            id = "test-c1", name = "Gobelin de test", type = CharacterType.MONSTRE,
            initiative = 12, maxHp = 7, currentHp = 7, ac = 13, speed = 9,
        )
        first.handleSavePreset(monster)
        check(first.presets.isNotEmpty())

        // Simulates an app restart: a brand new state instance should load what was persisted.
        val second = CombatTrackerState()
        assertEquals(first.presets.map { it.name }, second.presets.map { it.name })

        val preset = CharacterPreset(
            id = "p1", name = "Gobelin", type = CharacterType.MONSTRE,
            initiative = 12, maxHp = 7, ac = 15, speed = 9,
        )
        PresetStorage.save(SavedPresets(presets = listOf(preset)))
        val reloaded = PresetStorage.load()
        assertEquals(listOf(preset), reloaded.presets)
    }
}
