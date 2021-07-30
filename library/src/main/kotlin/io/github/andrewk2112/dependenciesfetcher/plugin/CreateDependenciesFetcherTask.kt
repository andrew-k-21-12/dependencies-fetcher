package io.github.andrewk2112.dependenciesfetcher.plugin

import io.github.andrewk2112.dependenciesfetcher.DependenciesFetcher
import io.github.andrewk2112.dependenciesfetcher.logging.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Creates an instance of [DependenciesFetcher] using provided input configs.
 * */
internal abstract class CreateDependenciesFetcherTask internal constructor() : DefaultTask() {

    /**
     * Creates an instance of [DependenciesFetcher].
     *
     * @return Created [DependenciesFetcher].
     *
     * @throws IllegalArgumentException If some argument was not supplied.
     * */
    @TaskAction
    @Throws(IllegalArgumentException::class)
    internal open fun createDependenciesFetcher(): DependenciesFetcher {
        val dependenciesDirectoryName = dependenciesDirectoryName.orNull
            ?: throw IllegalArgumentException("No dependencies directory name was supplied to the dependencies fetcher")
        val dependenciesDirectory = File(project.projectDir, dependenciesDirectoryName)
        return DependenciesFetcher(dependenciesDirectory, fetchingLogger.orNull)
    }

    /** Names the directory which will store all dependencies. */
    @get:Input
    abstract val dependenciesDirectoryName: Property<String>

    /** Logs dependencies fetching process. */
    @get:Input
    abstract val fetchingLogger: Property<Logger>

}
