package com.mineinabyss.geary.ecs.actions

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.components.PassiveActionsDisabledComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("geary:enable_passive_actions")
@AutoscanComponent
public class EnablePassiveActionsAction(public val no_param: Boolean = true) : GearyAction() {
    override fun GearyEntity.run(): Boolean {
        remove<PassiveActionsDisabledComponent>()
        return true
    }
}
