package io.github.andrewk2112.dependenciesfetcher

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import java.net.URI

/**
 * Simplifies creation of a GitHub repository configuration.
 * */
class GitHubRepositoryFactory {

    /**
     * Creates a configuration to reference GitHub repositories as dependencies.
     *
     * @param handler A [RepositoryHandler] to create repository configurations.
     *
     * @return An [IvyArtifactRepository]-based configuration to resolve GitHub repositories as dependencies.
     * */
    fun create(handler: RepositoryHandler): IvyArtifactRepository = handler.ivy {
        it.url = URI("https://github.com")
        it.patternLayout { layout ->
            layout.apply {
                artifact("[organisation]/[module]/releases/download/[revision]/[module]-[revision]-[classifier].[ext]")
                artifact("[organisation]/[module]/archive/[revision].[ext]")
            }
        }
        it.metadataSources { sources -> sources.artifact() }
    }

}
