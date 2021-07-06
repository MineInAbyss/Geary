package com.mineinabyss.geary.ecs.actions

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.components.EventsDisabledComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("geary:disable_events")
@AutoscanComponent
public class DisableEventsAction(public val no_param: Boolean = true) : GearyAction() {
    override fun GearyEntity.run(): Boolean {
        setPersisting(EventsDisabledComponent(true))
        return true
    }
}
