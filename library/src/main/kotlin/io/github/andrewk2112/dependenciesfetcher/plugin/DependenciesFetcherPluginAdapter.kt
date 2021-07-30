package io.github.andrewk2112.dependenciesfetcher.plugin

import io.github.andrewk2112.dependenciesfetcher.ArchiveType
import io.github.andrewk2112.dependenciesfetcher.DependenciesFetcher
import io.github.andrewk2112.dependenciesfetcher.logging.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * Provides APIs more convenient to be used in Gradle's [Plugin]s.
 * */
internal class DependenciesFetcherPluginAdapter internal constructor(private val project: Project) {

    // API.

    /**
     * Prepares an instance of the [DependenciesFetcher] to be used in [fetch] calls.
     *
     * @param dependenciesDirectoryName A directory name inside of the module's folder to save dependencies into.
     * @param fetchingLogger            An optional [Logger] to journal fetching status.
     * */
    @JvmOverloads
    fun configure(dependenciesDirectoryName: String, fetchingLogger: Logger? = null) {
        val dependenciesDir = File(project.projectDir, dependenciesDirectoryName)
        dependenciesFetcher = DependenciesFetcher(dependenciesDir, fetchingLogger)
    }

    /**
     * Performs fetching of a particular dependency.
     *
     * @param name        Destination name to store the dependency with.
     * @param url         URL to grab a dependency from.
     * @param archiveType An [ArchiveType] to unpack the dependency.
     *
     * @return An absolute path to the fetched dependency.
     *
     * @throws IllegalStateException If [configure] wasn't called before.
     * */
    @Throws(IllegalStateException::class)
    fun fetch(name: String, url: String, archiveType: ArchiveType): String =
        dependenciesFetcher?.fetchDependency(url, archiveType, name)?.absolutePath
            ?: throw IllegalStateException("Make sure to configure the fetcher first!")

    /**
     * Removes all fetched dependencies.
     * */
    fun cleanUpAllFetchedDependencies() = dependenciesFetcher?.cleanUpAllFetchedDependencies()





    // Private.

    /** An instance of the [DependenciesFetcher] to be used for all fetches. */
    private var dependenciesFetcher: DependenciesFetcher? = null

}
