package com.andrew_k_21_12.dependenciesfetcher

import java.io.File

/**
 * Performs a recursive deletion, only if this [File] is an accessible directory.
 *
 * @return True - if a recursive deletion has been successfully performed.
 * */
internal fun File.deleteRecursivelyIfIsAccessibleDirectory() =
    try { isDirectory } catch (_: SecurityException) { false } && deleteRecursively()
