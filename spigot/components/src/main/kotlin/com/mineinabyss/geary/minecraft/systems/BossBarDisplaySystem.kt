package com.mineinabyss.geary.minecraft.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.minecraft.components.DisplayBossBar
import org.bukkit.entity.Entity

/**
 * Handles displaying of boss bars to players in range.
 * Uses values from the DisplayBossBar component.
 */
public object BossBarDisplaySystem : TickingSystem(interval = 10) {
    private val QueryResult.bossbar by get<DisplayBossBar>()
    private val QueryResult.bukkitentity by get<BukkitEntity>()

    override fun QueryResult.tick() {
        val location = bukkitentity.location
        val playersInRange = location.getNearbyPlayers(bossbar.range).map { it.uniqueId }

        // Gets players to add and remove
        val addPlayers = playersInRange - bossbar.playersInRange
        val removePlayers = bossbar.playersInRange - playersInRange

        // Removes and adds the necessary players
        bossbar.playersInRange.removeAll(removePlayers)
        bossbar.playersInRange.addAll(addPlayers)

    }
}