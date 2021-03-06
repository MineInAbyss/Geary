package com.mineinabyss.geary.ecs.api

import kotlinx.serialization.Polymorphic
import java.util.*
import kotlin.reflect.KClass

/**
 * GearyComponents aren't an interface because we like the option to be able to add `Any` class as a component to an
 * entity. However, for clarity we create this typealias.
 *
 * Since [Any] is not inherently serializable like an interface, when expecting a [GearyComponent] to be serializable,
 * use the [Polymorphic] annotation.
 */
public typealias GearyComponent = Any

/** Type alias for entity IDs. */
public typealias GearyEntityId = ULong

/** Type alias for component IDs. Is the same as [GearyEntityId]. */
public typealias GearyComponentId = GearyEntityId

internal typealias ComponentClass = KClass<out GearyComponent>

public typealias GearyType = TreeSet<GearyComponentId>
