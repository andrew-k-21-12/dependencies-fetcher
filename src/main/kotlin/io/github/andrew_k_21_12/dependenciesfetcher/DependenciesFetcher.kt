package io.github.andrew_k_21_12.dependenciesfetcher

import java.io.File
import java.io.IOException

/**
 * Manages non-repository dependencies.
 *
 * @param dependenciesDir A directory to store all fetched dependencies in.
 * @param logger          An instance of [Logger] to journal all operations under the hood.
 * */
class DependenciesFetcher @JvmOverloads constructor(
    private val dependenciesDir: File,
    private val logger: Logger? = null
) {

    // API.

    /**
     * Fetches a dependency from its [sourceUrl] and stores it by the [destinationName].
     *
     * @param sourceUrl       URL to grab a dependency from.
     * @param archiveType     Represents the archiving algorithm to unpack the downloaded dependency.
     * @param destinationName Destination name to store the dependency with.
     *
     * @return Path to the unarchived dependency ready to be used.
     *
     * @throws IllegalStateException
     * @throws IOException
     * */
    @Throws(IllegalStateException::class, IOException::class)
    fun fetchDependency(
        sourceUrl: String,
        archiveType: ArchiveType,
        destinationName: String
    ): File {

        // Marking the dependency as processed.
        processedDependencies.add(destinationName)

        // Checking if the dependency is already prepared:
        // this is a very simple check - no checksums or other validations.
        val dependencyDir = constructDependencyDir(destinationName)
        if (dependencyDir.isDirectory)
            return dependencyDir

        // Checking the working directories.
        val tempDependencyDir = constructTempDependencyDir(destinationName)
        checkAndInitDependencyDirectories(dependencyDir, tempDependencyDir)

        // Downloading the dependency.
        val downloadedFile = constructDownloadingFile(destinationName)
        if (!FileDownloader(logger).downloadSync(sourceUrl, downloadedFile))
            throw IOException("Could not download the $destinationName dependency from $sourceUrl")

        // Unarchiving into the temp directory.
        if (!Unarchiver(logger).unarchiveSync(downloadedFile, tempDependencyDir, archiveType))
            throw IOException("Could not unarchive the downloaded dependency $destinationName")

        // Clean up - deleting the downloaded archive.
        downloadedFile.delete()

        // Dropping the temp directory.
        if (!tempDependencyDir.renameTo(dependencyDir))
            throw IOException(
                "Could not finalize fetching of the $destinationName dependency " +
                        "by dropping its temp directory"
            )

        return dependencyDir

    }

    /**
     * Removes all dependencies were previously fetched:
     * temp directories and downloaded files are included too.
     * */
    fun cleanUpAllFetchedDependencies() {

        // Iterating through all fetched dependencies.
        val processedDependenciesIterator = processedDependencies.iterator()
        while (processedDependenciesIterator.hasNext()) {
            val dependencyName = processedDependenciesIterator.next()
            processedDependenciesIterator.remove()

            // Deleting all dependency-related stuff.
            constructDependencyDir(dependencyName).deleteRecursivelyIfIsAccessibleDirectory()
            constructTempDependencyDir(dependencyName).deleteRecursivelyIfIsAccessibleDirectory()
            constructDownloadingFile(dependencyName).takeIf {
                try { it.isFile } catch (_: SecurityException) { false }
            }?.delete()

        }

    }





    // Private.

    /**
     * Creates an instance of [File] for a dependency's directory.
     *
     * @param destinationName Dependency's name.
     *
     * @return [File] pointing to the target dependency directory.
     * */
    private fun constructDependencyDir(destinationName: String) =
        File(dependenciesDir, destinationName)

    /**
     * Creates an instance of [File] for a dependency's temp directory.
     *
     * @param destinationName Dependency's name.
     *
     * @return [File] pointing to the target dependency's temp directory.
     * */
    private fun constructTempDependencyDir(destinationName: String) =
        File(dependenciesDir, destinationName + suffixForTempUnarchiving)

    /**
     * Creates an instance of [File] for a dependency's source file for downloading.
     *
     * @param destinationName Dependency's name.
     *
     * @return [File] pointing to the target dependency's downloading file.
     * */
    private fun constructDownloadingFile(destinationName: String) =
        File(dependenciesDir, destinationName + extensionForDownloads)

    /**
     * Checks if the destination dependency directories are available.
     * Makes sure the [tempDependencyDir] is created.
     *
     * @param dependencyDir     Destination dependency directory to be checked.
     * @param tempDependencyDir Temp unarchiving directory to be checked and created.
     *
     * @throws IllegalStateException
     * @throws IOException
     * */
    @Throws(IllegalStateException::class, IOException::class)
    private fun checkAndInitDependencyDirectories(dependencyDir: File, tempDependencyDir: File) {

        // Checking if the destination dependency path is captured by some file.
        if (dependencyDir.isFile)
            throw IllegalStateException(
                "The destination dependency path already exists and it's a file: " +
                        dependencyDir.absolutePath
            )

        // Checking if the temp unarchiving path is captured by some file.
        if (tempDependencyDir.isFile)
            throw IllegalStateException(
                "The temp unarchiving path already exists and it's a file: " +
                        tempDependencyDir.absolutePath
            )

        // Creating the temp unarchiving directory.
        if (!tempDependencyDir.exists()) {
            if (!tempDependencyDir.mkdirs())
                throw IOException(
                    "Could not create the temp unarchiving directory: " +
                            tempDependencyDir.absolutePath
                )
        }

    }

    /** Contains dependencies' names marked as processed (successfully or not). */
    private val processedDependencies = mutableSetOf<String>()





    // Configs.

    /** Extension to be appended to dependencies source files being downloaded. */
    private val extensionForDownloads = ".download"

    /** Suffix to mark temp unarchiving folders with. */
    private val suffixForTempUnarchiving = "_temp"

}
