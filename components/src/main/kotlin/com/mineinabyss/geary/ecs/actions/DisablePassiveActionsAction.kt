package com.mineinabyss.geary.ecs.actions

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.components.PassiveActionsDisabledComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("geary:disable_passive_actions")
@AutoscanComponent
public class DisablePassiveActionsAction(public val no_param: Boolean = true) : GearyAction() {
    override fun GearyEntity.run(): Boolean {
        setPersisting(PassiveActionsDisabledComponent(true))
        return true
    }
}
