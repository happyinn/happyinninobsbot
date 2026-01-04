package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

import java.io.Serializable

data class SubtitleStreamInfo(
    val index: Int,
    val title: String?,
    val codecName: String,
    val language: String?,
    val disposition: Int,
) : Serializable
