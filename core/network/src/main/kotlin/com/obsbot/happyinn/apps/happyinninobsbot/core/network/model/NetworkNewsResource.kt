package com.obsbot.happyinn.apps.happyinninobsbot.core.network.model

import android.annotation.SuppressLint
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Network representation of [NewsResource] when fetched from /Newsresources
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NetworkNewsResource(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String,
    val publishDate: Instant,
    val type: String,
    val topics: List<String> = emptyList(),
)

