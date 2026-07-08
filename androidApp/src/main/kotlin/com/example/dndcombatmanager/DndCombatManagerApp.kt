package com.example.dndcombatmanager

import android.app.Application
import com.example.dndcombatmanager.combat.storage.AndroidStorageContext

class DndCombatManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidStorageContext.appContext = this
    }
}
