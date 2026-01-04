package com.obsbot.happyinn.apps.happyinninobsbot.core.data.mappers

import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.VideoStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.VideoStreamInfo


fun VideoStreamInfoEntity.toVideoStreamInfo() = VideoStreamInfo(
    index = index,
    title = title,
    codecName = codecName,
    language = language,
    disposition = disposition,
    bitRate = bitRate,
    frameRate = frameRate,
    frameWidth = frameWidth,
    frameHeight = frameHeight,
)
