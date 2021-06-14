package com.andrew_k_21_12.dependenciesfetcher

import java.io.File

/**
 * Backbone for logging of various processes and interactions with dependencies.
 * */
interface Logger {

    /**
     * Triggered when dependency downloading has just started.
     *
     * @param sourceUrl Source URL of the dependency to be downloaded.
     * */
    fun downloadingStarted(sourceUrl: String) {}

    /**
     * Triggered when some new bytes have been fetched during dependency downloading.
     *
     * @param bytesDownloaded Number of downloaded bytes.
     * */
    fun downloadingProgressUpdated(bytesDownloaded: Long) {}

    /**
     * Triggered when dependency downloading has just completed.
     * */
    fun downloadingCompleted() {}

    /**
     * Triggered when dependency unarchiving has just started.
     *
     * @param sourceArchive Source archive [File] started to be processed.
     * @param entriesCount  Number of total entries to be unarchived.
     * */
    fun unarchivingStarted(sourceArchive: File, entriesCount: Int) {}

    /**
     * Triggered when some new entry has been unarchived.
     *
     * @param entryName        Name of an entry has been unarchived.
     * @param entriesProcessed Number of currently unarchived entries.
     * */
    fun unarchivingProgressUpdated(entryName: String, entriesProcessed: Int) {}

    /**
     * Triggered when dependency unarchiving has just completed.
     * */
    fun unarchivingCompleted() {}

}
