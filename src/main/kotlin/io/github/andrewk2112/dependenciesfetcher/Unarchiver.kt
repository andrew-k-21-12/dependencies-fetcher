package io.github.andrewk2112.dependenciesfetcher

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Helps to unarchive compressed contents.
 *
 * @param logger An instance of [Logger] to journal all unarchiving operations.
 * */
internal class Unarchiver internal constructor(private val logger: Logger?) {

    // API.

    /**
     * Unarchives a [sourceArchive] of the [archiveType] into some [destinationDir].
     * This method is blocking until the completion of the unarchiving.
     *
     * @param sourceArchive   Source archive [File] to be processed.
     * @param destinationDir  Destination directory to unarchive into: make sure it exists.
     * @param archiveType     [ArchiveType] to pick the correct unarchiving algorithm.
     *
     * @return True - on successful unarchiving.
     * */
    internal fun unarchiveSync(
        sourceArchive: File,
        destinationDir: File,
        @Suppress("UNUSED_PARAMETER")
        archiveType: ArchiveType
    ): Boolean {

        // Reading archive's metadata.
        try { ZipFile(sourceArchive) } catch (_: IOException) { return false }.use { zipFile ->

            // Preparing and iterating through archived entries.
            val zipEntries = try { zipFile.entries().toList() }
                             catch (_: IllegalStateException) { return false }
            logger?.unarchivingStarted(sourceArchive, zipEntries.size)
            for (zipEntry in zipEntries.withIndex()) {
                if (!unarchiveZipEntry(zipFile, zipEntry, destinationDir))
                    return false
            }
            logger?.unarchivingCompleted()

        }

        return true

    }





    // Private.

    /**
     * Performs unarchiving of a single entry.
     *
     * @param zipFile        Source [ZipFile].
     * @param zipEntry       A [ZipEntry] to be unarchived.
     * @param destinationDir Unarchiving destination directory.
     *
     * @return True - on successful unarchiving.
     * */
    private fun unarchiveZipEntry(
        zipFile: ZipFile,
        zipEntry: IndexedValue<ZipEntry>,
        destinationDir: File
    ): Boolean {

        // Processing a directory creation: it should be created only if it doesn't exist.
        if (zipEntry.value.isDirectory) {
            val dirToMake = File(destinationDir, zipEntry.value.name)
            if (dirToMake.isDirectory) {
                deliverProgress(zipEntry)
                return true
            }
            // Exists, but it's not a directory, or the directory couldn't be created.
            if (dirToMake.exists() || !dirToMake.mkdir())
                return false
            deliverProgress(zipEntry)
        }

        // Processing file unarchiving.
        else {
            try { zipFile.getInputStream(zipEntry.value) }
            catch (_: Throwable) { return false }.use { input ->
                try {
                    Files.copy(
                        input,
                        File(destinationDir, zipEntry.value.name).toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }
                catch (_: Throwable) { return false }
                deliverProgress(zipEntry)
            }
        }

        return true

    }

    /**
     * Delivers the current unarchiving progress.
     *
     * @param zipEntry A [ZipEntry] has just been unarchived.
     * */
    private fun deliverProgress(zipEntry: IndexedValue<ZipEntry>) =
        logger?.unarchivingProgressUpdated(zipEntry.value.name, zipEntry.index + 1)

}
