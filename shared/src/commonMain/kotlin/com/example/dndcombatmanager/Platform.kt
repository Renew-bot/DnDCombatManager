package com.example.dndcombatmanager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform