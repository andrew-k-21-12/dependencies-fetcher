package io.github.andrewk2112.dependenciesfetcher

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.IvyArtifactRepository

/**
 * Simplifies declaration of the GitHub repository in Gradle Groovy build scripts.
 * */
class RepositoryHandlerExtension {

    /**
     * Wraps {@link GitHubRepositoryFactory#create(RepositoryHandler)} and does the same.
     * */
    static IvyArtifactRepository gitHub(RepositoryHandler handler) {
        return new GitHubRepositoryFactory().create(handler)
    }

}
