package com.mineinabyss.geary.ecs.api.entities

import com.mineinabyss.geary.ecs.api.GearyComponent
import com.mineinabyss.geary.ecs.api.GearyEntityId
import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.ecs.engine.ENTITY_MASK

/** Gets the entity associated with [id] and runs code on it. */
public inline fun geary(id: GearyEntityId, run: GearyEntity.() -> Unit): GearyEntity =
    geary(id).apply(run)

/** Gets the entity associated with [id]. */
//TODO think a bit more about the benefits of automatically adding a mask vs possible bugs with internal code
// working with relations accidentally using this
@Suppress("NOTHING_TO_INLINE")
public inline fun geary(id: GearyEntityId): GearyEntity = GearyEntity(id and ENTITY_MASK)

@Suppress("NOTHING_TO_INLINE")
public inline fun gearyNoMask(id: GearyEntityId): GearyEntity = GearyEntity(id)

/** Gets the entity associated with [id]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun geary(id: Long): GearyEntity = GearyEntity(id.toULong() and ENTITY_MASK)

/**
 * Swaps components of type [T] on two entities.
 *
 * @return Whether or not at least one component of type [T] was present and swapped places.
 */
public inline fun <reified T : GearyComponent> GearyEntity?.swapComponent(with: GearyEntity?): Boolean {
    val component = this?.get<T>()
    val otherComponent = with?.get<T>()

    if (component != null)
        with?.set(component)
    else
        with?.remove<T>()

    if (otherComponent != null)
        this?.set(otherComponent)
    else
        this?.remove<T>()

    return component != null || otherComponent != null
}

public fun Collection<GearyComponent>.createEntity(): GearyEntity {
    return Engine.entity {
        setAll(this@createEntity)
        //TODO special components for serializing parents, prefabs, etc
    }
}
