package com.mineinabyss.geary.minecraft.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BarColor
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.*

/**
 * > geary:bossbar
 *
 * Holds properties of a boss bar (colour, style, range)
 */
@Serializable
@SerialName("geary:bossbar")
@AutoscanComponent
public class DisplayBossBar(
    public val color: BarColor,
    public val style: BarStyle,
    public val range: Double,
) {
    @Transient public val bossBar: BossBar = Bukkit.createBossBar("Boss Bar", color, style)
    @Transient public val playersInRange: MutableSet<UUID> = mutableSetOf<UUID>()
}
