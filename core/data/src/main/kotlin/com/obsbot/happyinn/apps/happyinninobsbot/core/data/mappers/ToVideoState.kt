package com.obsbot.happyinn.apps.happyinninobsbot.core.data.mappers

import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.VideoState
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.converter.UriListConverter
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.MediumStateEntity


fun MediumStateEntity.toVideoState(): VideoState {
    return VideoState(
        path = uriString,
        position = playbackPosition.takeIf { it != 0L },
        audioTrackIndex = audioTrackIndex,
        subtitleTrackIndex = subtitleTrackIndex,
        playbackSpeed = playbackSpeed,
        externalSubs = UriListConverter.fromStringToList(externalSubs),
        videoScale = videoScale,
    )
}
