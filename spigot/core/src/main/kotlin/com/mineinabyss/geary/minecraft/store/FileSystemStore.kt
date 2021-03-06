package com.mineinabyss.geary.minecraft.store

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.serialization.Formats
import kotlinx.serialization.BinaryFormat
import java.io.IOException
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

public class FileSystemStore(
    private val root: Path,
    private val format: BinaryFormat = Formats.cborFormat,
) : GearyStore {
    override fun encode(entity: GearyEntity): ByteArray {
        return format.encodeToByteArray(
            GearyStore.componentsSerializer,
            entity.getPersistingComponents()
        )
    }

    override fun decode(entity: GearyEntity, uuid: UUID) {
        try {
            val bytes = read(uuid) ?: return
            entity.apply {
                setAllPersisting(
                    format.decodeFromByteArray(
                        GearyStore.componentsSerializer,
                        bytes
                    )
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }

    override fun read(uuid: UUID): ByteArray? {
        val file = (root / uuid.toString())
        if (file.notExists()) return null
        return file.readBytes()
    }

    override fun write(entity: GearyEntity, bytes: ByteArray) {
        val uuid = entity.getOrSet { UUID.randomUUID() }
        val encoded = encode(entity)
        val file = (root / uuid.toString())
        file.createFile().writeBytes(encoded)
    }
}
