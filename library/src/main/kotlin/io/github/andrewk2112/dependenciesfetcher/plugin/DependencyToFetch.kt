package io.github.andrewk2112.dependenciesfetcher.plugin

import io.github.andrewk2112.dependenciesfetcher.ArchiveType
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * A model representing all properties required to fetch a dependency.
 *
 * @param name Dependency's name.
 * */
internal abstract class DependencyToFetch @Inject internal constructor(val name: String) {

    /** URL to fetch the dependency from. */
    abstract val url: Property<String>

    /** [ArchiveType] to unpack the dependency. */
    abstract val archiveType: Property<ArchiveType>

}
