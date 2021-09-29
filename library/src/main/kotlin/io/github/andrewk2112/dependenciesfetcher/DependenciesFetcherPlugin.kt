package io.github.andrewk2112.dependenciesfetcher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Copy

/**
 * Does all processing required to integrate dependencies fetching features into a particular project.
 * */
private class DependenciesFetcherPlugin : Plugin<Project> {

    // Override.

    /**
     * An entry point for all plugin-related configurations.
     *
     * @param project A target [Project] to apply the plugin to.
     * */
    override fun apply(project: Project) {
        val configurationForDependencies = project.configurations.create(configurationName)
        registerUnpackDependenciesTask(project, configurationForDependencies)
    }





    // Private.

    /**
     * Creates a task to unpack target dependencies and inserts it into the tasks graph.
     *
     * @param project             In this case a [Project] object is a kind of context
     *                            to describe the task's configuration.
     * @param sourceConfiguration A target [Configuration] to grab and unpack all dependencies from.
     * */
    private fun registerUnpackDependenciesTask(project: Project, sourceConfiguration: Configuration) {
        val projectTaskContainer = project.tasks
        val registeredTask = projectTaskContainer.register(taskName, Copy::class.java) { copy ->
            sourceConfiguration.forEach {
                val fileName = it.name
                val sourceFileTree = when {
                    fileName.endsWith("zip")    -> project.zipTree(it)
                    fileName.endsWith("tar.gz") -> project.tarTree(it)
                    else                        -> throw IllegalArgumentException(
                                                   "The dependency artifact $fileName has an unsupported extension"
                                                   )
                }
                copy.from(sourceFileTree)
            }
            copy.into(project.layout.buildDirectory.dir(destinationFolderName))
        }
        projectTaskContainer.named("preBuild").get().dependsOn(registeredTask)
    }





    // Configs.

    /** The name for the [Configuration] to state dependencies to be fetched and unpacked. */
    private val configurationName = "unpackOnly"

    /** Names the task to unpack all target dependencies. */
    private val taskName = "unpackDependencies"

    /** The name of the destination folder to unpack all target dependencies into. */
    private val destinationFolderName = "unpacked"

}
