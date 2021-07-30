package io.github.andrewk2112.dependenciesfetcher.plugin

import io.github.andrewk2112.dependenciesfetcher.ArchiveType
import io.github.andrewk2112.dependenciesfetcher.logging.SimplePrintingLogger
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionContainer

/**
 * Declares and configures all entities to include the [io.github.andrewk2112.dependenciesfetcher.DependenciesFetcher]
 * into buildscripts using Gradle plugin APIs.
 * */
internal class DependenciesFetcherPlugin internal constructor() : Plugin<Project> {

    // Override.

    /**
     * Entry point for all plugin-related configs.
     *
     * @param project Target project's data.
     *
     * @throws IllegalArgumentException
     * */
    @Throws(IllegalArgumentException::class)
    override fun apply(project: Project) {

        // Extensions shortcut.
        val extensions = project.extensions

        // Exposing all buildscript imports.
        exposeImports(extensions)

        // Exposing an adapter to handle the fetcher.
        extensions.extraProperties.set(fetcherExtensionName, DependenciesFetcherPluginAdapter(project))

        /*
        // Creating an extension block to declare target dependencies.
        val dependencyToFetchContainer = createDependenciesContainer(objects, extensions)

        // Creating a task to prepare an instance of the fetcher and run fetching.
        createMainTask(project, dependencyToFetchContainer)
        */

    }





    // Private.

    /**
     * Declares import statements to be exposed into buildscripts.
     *
     * @param extensions Extensions to add imports into.
     * */
    private fun exposeImports(extensions: ExtensionContainer) {
        extensions.extraProperties.apply {
            set(SimplePrintingLogger::class.java.simpleName, SimplePrintingLogger::class.java)
            set(ArchiveType.ZIP.name, ArchiveType.ZIP)
        }
    }

    /**
     * Describes the container used to declare dependencies inside.
     *
     * @param objects    Project's objects.
     * @param extensions Project's extensions.
     *
     * @return Created container.
     * */
    @Deprecated("Not needed for now, but maybe one day we'll use these APIs")
    private fun createDependenciesContainer(
        objects: ObjectFactory,
        extensions: ExtensionContainer
    ): NamedDomainObjectContainer<DependencyToFetch> {
        // The name in the factory comes from buildscript declarations.
        val dependencyToFetchContainer = objects.domainObjectContainer(DependencyToFetch::class.java) { name ->
            objects.newInstance(DependencyToFetch::class.java, name)
        }
        extensions.add(extensionNameDependencies, dependencyToFetchContainer)
        return dependencyToFetchContainer
    }

    /**
     * Declares the main task to configure and launch the fetcher.
     *
     * @param project                    Project's data.
     * @param dependencyToFetchContainer Container with described dependencies.
     *
     * @throws IllegalArgumentException
     * */
    @Deprecated("Not needed for now, but maybe one day we'll use these APIs")
    @Throws(IllegalArgumentException::class)
    private fun createMainTask(
        project: Project,
        dependencyToFetchContainer: NamedDomainObjectContainer<DependencyToFetch>
    ) {

        // Declaring a task to configure and launch the fetcher.
        val mainTaskProvider = project.tasks.register(taskNameMain, CreateDependenciesFetcherTask::class.java)

        // Starting to fetch dependencies after evaluation of the project.
        project.afterEvaluate {

            // Creating the fetcher according to provided configs.
            val dependenciesFetcher = mainTaskProvider.get().createDependenciesFetcher()

            // Fetching each declared dependency.
            dependencyToFetchContainer.all { dependencyToFetch ->

                // Fetching the dependency.
                val name        = dependencyToFetch.name
                val sourceUrl   = dependencyToFetch.url.orNull
                               ?: throw IllegalArgumentException("No source URL has been provided to fetch $name")
                val archiveType = dependencyToFetch.archiveType.orNull
                               ?: throw IllegalArgumentException("No archive type has been provided to unpack $name")
                dependenciesFetcher.fetchDependency(sourceUrl, archiveType, name)

            }

        }

    }

    /** Name to access a [DependenciesFetcherPluginAdapter] from buildscripts. */
    private val fetcherExtensionName = "dependenciesFetcher"

    /** [io.github.andrewk2112.dependenciesfetcher.DependenciesFetcher] configuring task name. */
    private val taskNameMain = "dependenciesFetcher"

    /** Name of the container to declare required dependencies inside. */
    private val extensionNameDependencies = "dependenciesToFetch"

}
