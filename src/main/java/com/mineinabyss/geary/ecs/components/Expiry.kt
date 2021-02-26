package com.mineinabyss.geary.ecs.components

public class Expiry(
    duration: Long
) {
    public val endTime: Long = System.currentTimeMillis() + duration
    public fun timeOver(): Boolean = TODO()
    public operator fun component1(): Long = endTime
}
