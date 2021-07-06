package com.mineinabyss.geary.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.PassiveActionsComponent
import com.mineinabyss.geary.ecs.components.PassiveActionsDisabledComponent
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult

/**
 * A system that runs actions from [PassiveActionsComponent]s every 20 ticks.
 */
public object PassiveActionsSystem : TickingSystem(interval = 20) {
    private val QueryResult.actions by get<PassiveActionsComponent>()
    init {
       not {
           has<PassiveActionsDisabledComponent>()
       }
    }

    override fun QueryResult.tick() {
        actions.wrapped.forEach {
            it.runOn(entity)
        }
    }
}
