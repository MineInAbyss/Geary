package com.mineinabyss.geary.ecs.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.engine.componentId
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.helper.toComponentClasses
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * > geary:entity
 *
 * Does checks against the components of an entity. For example, whether it has a list of components.
 */
@Serializable
@SerialName("geary:entity")
public class EntityComponentConditions(
    @SerialName("has")
    public val componentNames: Set<String> = emptySet(),
): GearyCondition() {
    private val componentClasses by lazy { componentNames.toComponentClasses() }

    override fun GearyEntity.check(): Boolean =
        hasAll(componentClasses)
}


@Serializable
@SerialName("geary:entity_not")
public class EntityHasNoComponentConditions(
    @SerialName("has")
    public val componentNames: Set<String> = emptySet(),
) : GearyCondition() {
    private val componentClasses by lazy { componentNames.toComponentClasses() }

    override fun GearyEntity.check(): Boolean =
        !componentClasses.any { has(componentId(it)) }
}
