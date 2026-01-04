package com.obsbot.happyinn.apps.happyinninobsbot.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.DirectoryEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.MediumEntity

//TODO 待细看
data class DirectoryWithMedia(
    @Embedded val directory: DirectoryEntity,
    @Relation(
        entity = MediumEntity::class,
        parentColumn = "path",
        entityColumn = "parent_path",
    )
    val media: List<MediumWithInfo>,
)
