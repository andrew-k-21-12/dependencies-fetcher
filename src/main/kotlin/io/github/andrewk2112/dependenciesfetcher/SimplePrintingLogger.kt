package io.github.andrewk2112.dependenciesfetcher

import java.io.File

/**
 * This is a simple implementation of [Logger]
 * which just prints all messages about the current progress into the console.
 * */
class SimplePrintingLogger : Logger {

    // Overrides.

    override fun downloadingStarted(sourceUrl: String) {
        latestDownloadingProgressMB = 0
        println("Started to download from $sourceUrl")
    }

    override fun downloadingProgressUpdated(bytesDownloaded: Long) {
        val megabytesDownloaded = bytesDownloaded / (1024 * 1024)
        if (latestDownloadingProgressMB != megabytesDownloaded) {
            latestDownloadingProgressMB = megabytesDownloaded
            println("Megabytes downloaded: $megabytesDownloaded")
        }
    }

    override fun downloadingCompleted() = println("Downloading has been completed")

    override fun unarchivingStarted(sourceArchive: File, entriesCount: Int) {
        unarchivingEntriesCount = entriesCount
        println("Started to unarchive the downloaded file ${sourceArchive.absolutePath}")
    }

    override fun unarchivingProgressUpdated(entryName: String, entriesProcessed: Int) =
        println("Unarchived ($entriesProcessed/$unarchivingEntriesCount): $entryName")

    override fun unarchivingCompleted() = println("Unarchiving has been completed")





    // Private.

    /** Keeps the latest notified amount of downloaded megabytes. */
    private var latestDownloadingProgressMB = 0L

    /** Keeps the total entries count for the ongoing unarchiving. */
    private var unarchivingEntriesCount = 0

}
