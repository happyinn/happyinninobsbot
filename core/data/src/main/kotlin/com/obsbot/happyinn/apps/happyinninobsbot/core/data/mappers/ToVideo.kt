package com.obsbot.happyinn.apps.happyinninobsbot.core.data.mappers

import com.obsbot.happyinn.apps.happyinninobsbot.Utils
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.AudioStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.SubtitleStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.relations.MediumWithInfo
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Video
import java.util.Date

fun MediumWithInfo.toVideo() = Video(
    id = mediumEntity.mediaStoreId,
    path = mediumEntity.path,
    parentPath = mediumEntity.parentPath,
    duration = mediumEntity.duration,
    uriString = mediumEntity.uriString,
    nameWithExtension = mediumEntity.name,
    width = mediumEntity.width,
    height = mediumEntity.height,
    size = mediumEntity.size,
    dateModified = mediumEntity.modified,
    format = mediumEntity.format,
    thumbnailPath = mediumEntity.thumbnailPath,
    playbackPosition = mediumStateEntity?.playbackPosition ?: 0L,
    lastPlayedAt = mediumStateEntity?.lastPlayedTime?.let { Date(it) },
    formattedDuration = Utils.formatDurationMillis(mediumEntity.duration),
    formattedFileSize = Utils.formatFileSize(mediumEntity.size),
    videoStream = videoStreamInfo?.toVideoStreamInfo(),
    audioStreams = audioStreamsInfo.map(AudioStreamInfoEntity::toAudioStreamInfo),
    subtitleStreams = subtitleStreamsInfo.map(SubtitleStreamInfoEntity::toSubtitleStreamInfo),
)
