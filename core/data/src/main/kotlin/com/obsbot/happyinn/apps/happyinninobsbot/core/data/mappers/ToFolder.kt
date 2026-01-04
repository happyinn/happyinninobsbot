package com.obsbot.happyinn.apps.happyinninobsbot.core.data.mappers

import com.obsbot.happyinn.apps.happyinninobsbot.Utils
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.relations.DirectoryWithMedia
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.relations.MediumWithInfo
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder


fun DirectoryWithMedia.toFolder() = Folder(
    name = directory.name,
    path = directory.path,
    dateModified = directory.modified,
    parentPath = directory.parentPath,
    formattedMediaSize = Utils.formatFileSize(media.sumOf { it.mediumEntity.size }),
    mediaList = media.map(MediumWithInfo::toVideo),
)
