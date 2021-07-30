package io.github.andrewk2112.dependenciesfetcher.internal

import java.io.File

/**
 * Performs a recursive deletion, only if this [File] is an accessible directory.
 *
 * @return True - if a recursive deletion has been successfully performed.
 * */
internal fun File.deleteRecursivelyIfIsAccessibleDirectory(): Boolean =
    try { isDirectory } catch (_: SecurityException) { false } && deleteRecursively()
