import io.github.andrewk2112.dependenciesfetcher.GitHubRepositoryFactory
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.IvyArtifactRepository

/**
 * Syntax sugar to declare a GitHub repositories connection simpler in KTS.
 *
 * @return An instance of [IvyArtifactRepository] configured to resolve GitHub repositories.
 * */
fun RepositoryHandler.gitHub(): IvyArtifactRepository = GitHubRepositoryFactory().create(this)
