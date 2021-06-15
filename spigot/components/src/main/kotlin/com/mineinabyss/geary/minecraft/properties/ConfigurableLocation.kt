package com.mineinabyss.geary.minecraft.properties

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.properties.EntityProperty
import com.mineinabyss.geary.ecs.components.Source
import com.mineinabyss.geary.minecraft.access.toBukkit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Base abstract class for getting a location given an entity.
 *
 * Using polymorphic serialization, we can define and configure subclasses of this class to get locations
 * their own ways.
 *
 * Useful for actions that wish to run at a specific location but want to remain configurable.
 */
@Serializable
public sealed class ConfigurableLocation : EntityProperty<Location>()

/**
 * > source.location
 *
 * Gets the location of the bukkit entity associated with this entity.
 */
@Serializable
@SerialName("source.location")
public class AtSourceLocation : ConfigurableLocation() {
    override fun GearyEntity.read(): Location? =
        get<Source>()?.entity?.toBukkit()?.location
}

/**
 * > entity.location
 *
 * Gets the location of the bukkit entity associated with this entity.
 */
@Serializable
@SerialName("entity.location")
public class AtEntityLocation : ConfigurableLocation() {
    override fun GearyEntity.read(): Location? =
        toBukkit()?.location
}

/**
 * > player.location
 *
 * Gets the location of the player associated with the entity.
 */
@Serializable
@SerialName("player.location")
public class AtPlayerLocation : ConfigurableLocation() {
    override fun GearyEntity.read(): Location? =
        toBukkit<Player>()?.location
}

/**
 * > player.target_block
 *
 * Gets the location of the target block the player associated with the entity is looking at.
 *
 * @param maxDist The maximum distance this can extend.
 * @param allowAir Whether to allow clicking on nothing.
 */
@Serializable
@SerialName("player.target_block")
public class AtPlayerTargetBlock(
    private val maxDist: Int = 3,
    private val allowAir: Boolean = false,
    private val onFace: Boolean = false,
) : ConfigurableLocation() {
    override fun GearyEntity.read(): Location? {
        with<Player> { player ->
            val block = if (onFace)
                player.getLastTwoTargetBlocks(null, maxDist).first()
            else
                player.getTargetBlockExact(maxDist) ?: return null
            if (!allowAir && !block.isEmpty) return null
            return block.location
        }
        return null
    }
}
