package com.mineinabyss.geary.ecs.engine

import com.mineinabyss.geary.ecs.api.GearyComponent
import com.mineinabyss.geary.ecs.api.GearyComponentId
import com.mineinabyss.geary.ecs.api.GearyEntityId
import com.mineinabyss.geary.ecs.api.GearyType
import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.ecs.api.entities.geary
import com.mineinabyss.geary.ecs.api.relations.Relation
import com.mineinabyss.geary.ecs.api.relations.RelationParent
import com.mineinabyss.geary.ecs.api.systems.QueryManager
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.ComponentInfo
import com.mineinabyss.geary.ecs.entities.children
import com.mineinabyss.idofront.messaging.logError
import java.util.*
import kotlin.reflect.KClass

/**
 * The default implementation of Geary's Engine.
 *
 * This engine uses [Archetype]s. Each component is an entity itself with an id associated with it.
 * We keep track of each entity's components in the form of it's [GearyType] stored in the [typeMap].
 *
 * Learn more [here](https://github.com/MineInAbyss/Geary/wiki/Basic-ECS-engine-architecture).
 */
public open class GearyEngine : TickingEngine() {
    internal val typeMap = mutableMapOf<GearyEntityId, Record>()
    private var currId: GearyEntityId = 0uL

    //TODO there's likely a more performant option
    private val removedEntities = Stack<GearyEntityId>()

    private val classToComponentMap = mutableMapOf<KClass<*>, GearyEntityId>()

    init {
        //Register an entity for the ComponentInfo component, otherwise getComponentIdForClass does a StackOverflow
        val componentInfo = entity()
        classToComponentMap[ComponentInfo::class] = componentInfo.id
//        componentInfo.set(ComponentInfo(ComponentInfo::class)) //FIXME causes an error
    }

    override fun getComponentIdForClass(kClass: KClass<*>): GearyComponentId {
        return classToComponentMap.getOrPut(kClass) {
            entity {
                set(ComponentInfo(kClass))
            }.id
        }
    }

    //TODO Proper pipeline with different stages
    protected val registeredSystems: MutableList<TickingSystem> = mutableListOf()

    override fun addSystem(system: TickingSystem): Boolean {
        QueryManager.trackQuery(system)
        return registeredSystems.add(system)
    }

    //TODO support suspending functions for systems
    // perhaps async support in the future
    override fun tick(currentTick: Long) {
        registeredSystems
            .filter { currentTick % it.interval == 0L }
            .forEach {
                try {
                    it.runSystem()
                } catch (e: Exception) {
                    logError("Error while running system ${it.javaClass.name}")
                    e.printStackTrace()
                }
            }
    }

    /** Describes how to individually tick each system */
    protected open fun TickingSystem.runSystem() {
        doTick()
    }

    override fun scheduleSystemTicking() {
        TODO("Implement a system for ticking independent of spigot")
    }


    @Synchronized
    override fun getNextId(): GearyEntityId = if (removedEntities.isNotEmpty()) removedEntities.pop() else currId++

    override fun getComponentsFor(entity: GearyEntityId): Set<GearyComponent> =
        getRecord(entity)?.run {
            archetype.getComponents(row).toSet()
        } ?: emptySet()

    override fun getRelatedComponentsFor(
        entity: GearyEntityId,
        relationParent: RelationParent
    ): Set<GearyComponent> = getRecord(entity)?.run {
        archetype
            .relations[relationParent.id.toLong()]
            ?.mapNotNullTo(mutableSetOf()) { archetype[row, it.component] }
    } ?: setOf()


    override fun addComponentFor(entity: GearyEntityId, component: GearyComponentId) {
        getOrAddRecord(entity).apply {
            val record = archetype.addComponent(entity, this, HOLDS_DATA.inv() and component)
            typeMap[entity] = record ?: return
        }
    }

    override fun setComponentFor(entity: GearyEntityId, component: GearyComponentId, data: GearyComponent) {
        getOrAddRecord(entity).apply {
            // Only add HOLDS_DATA if this isn't a relation. All relations implicitly hold data currently and that bit
            // corresponds to the component part of the relation.
            val role = if (!component.hasRole(RELATION)) HOLDS_DATA else NO_ROLE
            val record = archetype.setComponent(entity, this, component.withRole(role), data)
            typeMap[entity] = record ?: return
        }
    }

    override fun setRelationFor(
        entity: GearyEntityId,
        parent: RelationParent,
        forComponent: GearyComponentId,
        data: GearyComponent
    ) {
        setComponentFor(entity, Relation(parent, forComponent).id, data)
    }

    override fun removeComponentFor(entity: GearyEntityId, component: GearyComponentId): Boolean {
        return getRecord(entity)?.apply {
            val record = archetype.removeComponent(entity, this, component.withRole(HOLDS_DATA))
                ?: archetype.removeComponent(entity, this, component)
            typeMap[entity] = record ?: return false
        } != null
    }

    override fun getComponentFor(entity: GearyEntityId, component: GearyComponentId): GearyComponent? =
        getRecord(entity)?.run { archetype[row, component.withRole(HOLDS_DATA)] }

    override fun hasComponentFor(entity: GearyEntityId, component: GearyComponentId): Boolean =
        getRecord(entity)?.archetype?.contains(component) ?: false

    //TODO might be a smarter way of storing removed entities as an implicit list within a larger list of entities eventually
    override fun removeEntity(entity: GearyEntityId) {
        // remove all children of this entity from the ECS as well
        geary(entity).apply {
            children.forEach { it.removeEntity() }
        }

        getRecord(entity)?.apply {
            archetype.removeEntity(row)
        }
        typeMap.remove(entity)

        //add current id into queue for reuse
        removedEntities.push(entity)
    }

    public override fun getType(entity: GearyEntityId): GearyType = typeMap[entity]?.archetype?.type ?: GearyType()

    public override fun getRecord(entity: GearyEntityId): Record? = typeMap[entity]

    override fun setRecord(entity: GearyEntityId, record: Record) {
        typeMap[entity] = record
    }

    private fun getOrAddRecord(entity: GearyEntityId) =
        typeMap.getOrPut(entity) { root.addEntityWithData(entity, listOf()) }
}
