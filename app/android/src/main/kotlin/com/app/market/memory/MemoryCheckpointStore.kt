package com.app.market.memory

import android.content.Context
import android.util.AtomicFile
import com.app.market.ui.navigation.NavigationCheckpoint
import java.io.File
import java.io.FileNotFoundException

internal class MemoryCheckpointStore(context: Context) {
    private val file = AtomicFile(File(context.noBackupFilesDir, "memory-checkpoint.json"))

    @Volatile
    var currentNavigation: NavigationCheckpoint? = null

    @Synchronized
    fun save(): Boolean {
        val state = currentNavigation ?: return false
        val bytes = state.encode().encodeToByteArray()
        if (bytes.size > MAX_BYTES) return false
        val stream = file.startWrite()
        try {
            stream.write(bytes)
            file.finishWrite(stream)
        } catch (error: Exception) {
            file.failWrite(stream)
            throw error
        }
        return true
    }

    @Synchronized
    fun take(): NavigationCheckpoint? {
        try {
            return file.openRead().use { stream ->
                if (stream.channel.size() > MAX_BYTES) return null
                val bytes = stream.readBytes()
                if (bytes.size > MAX_BYTES) null else NavigationCheckpoint.decode(bytes.decodeToString())
            }
        } catch (_: FileNotFoundException) {
            return null
        } finally {
            file.delete()
        }
    }

    private companion object {
        const val MAX_BYTES = 64 * 1024
    }
}
