package com.obsbot.happyinn.apps.happyinninobsbot.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.AudioStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.MediumEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.MediumStateEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.SubtitleStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.VideoStreamInfoEntity

data class MediumWithInfo(
    @Embedded val mediumEntity: MediumEntity,
    @Relation(
        parentColumn = "uri",
        entityColumn = "uri",
    )
    val mediumStateEntity: MediumStateEntity?,
    @Relation(
        parentColumn = "uri",
        entityColumn = "medium_uri",
    )
    val videoStreamInfo: VideoStreamInfoEntity?,
    @Relation(
        parentColumn = "uri",
        entityColumn = "medium_uri",
    )
    val audioStreamsInfo: List<AudioStreamInfoEntity>,
    @Relation(
        parentColumn = "uri",
        entityColumn = "medium_uri",
    )
    val subtitleStreamsInfo: List<SubtitleStreamInfoEntity>,
)
