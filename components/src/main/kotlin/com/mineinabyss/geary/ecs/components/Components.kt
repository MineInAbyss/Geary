package com.mineinabyss.geary.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:events_disabled")
@AutoscanComponent
public data class EventsDisabledComponent(val no_param: Boolean = true)

@Serializable
@SerialName("looty:passive_action_disabled")
@AutoscanComponent
public data class PassiveActionsDisabledComponent(val no_param: Boolean = true)
