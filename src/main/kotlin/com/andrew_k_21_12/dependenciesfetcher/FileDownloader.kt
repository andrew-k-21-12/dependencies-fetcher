package com.andrew_k_21_12.dependenciesfetcher

import java.io.*
import java.net.MalformedURLException
import java.net.URL

/**
 * Downloads files from a network.
 *
 * @param logger An instance of [Logger] to journal all downloading operations.
 * */
internal class FileDownloader internal constructor(private val logger: Logger?) {

    // API.

    /**
     * Downloads a file from a [sourceUrl] into a [destinationFile].
     * This method is blocking until the completion of the downloading.
     *
     * @param sourceUrl       Source URL of the file to be downloaded.
     * @param destinationFile Destination [File] to download into: make sure the path to it exists.
     *
     * @return True - on successful download.
     * */
    internal fun downloadSync(
        sourceUrl: String,
        destinationFile: File
    ): Boolean {

        // Preparing the source and the destination.
        val input  = openInputStream(sourceUrl) ?: return false
        val output = try { FileOutputStream(destinationFile) }
                     catch (_: FileNotFoundException) { return false }

        // Making sure to close the streams.
        input.use { i ->
            output.use { o ->

                logger?.downloadingStarted(sourceUrl)

                // Performing buffered downloading.
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                while (true) {
                    bytesRead = try { i.read(buffer, 0, buffer.size) }
                                catch (_: IOException) { return false }
                    if (bytesRead == -1)
                        break
                    try { o.write(buffer, 0, bytesRead) }
                    catch (_: IOException) { return false }

                    // Delivering the progress.
                    deliverProgress(destinationFile)

                }

                // Waiting to make sure all buffered bytes are written to the destination.
                o.flush()

                logger?.downloadingCompleted()

            }
        }

        return true

    }





    // Private.

    /**
     * Opens an [InputStream] from a raw URL string.
     *
     * @param sourceUrl Raw URL string to open an [InputStream] from.
     *
     * @return Opened [InputStream] or null if something went wrong.
     * */
    private fun openInputStream(sourceUrl: String): InputStream? {
        val url = try { URL(sourceUrl) }   catch (_: MalformedURLException) { return null }
        return    try { url.openStream() } catch (_: IOException)           { return null }
    }

    /**
     * Delivers the current downloading progress of the [destinationFile] via the [logger].
     *
     * @param destinationFile File to get the length of.
     * */
    private fun deliverProgress(destinationFile: File) {
        if (logger == null)
            return
        val totalBytesRead = try { destinationFile.length() }
                             catch (_: SecurityException) { return }
        logger.downloadingProgressUpdated(totalBytesRead)
    }

    /** Size of the buffer to perform downloading, in bytes. */
    private val bufferSize = 1024

}
