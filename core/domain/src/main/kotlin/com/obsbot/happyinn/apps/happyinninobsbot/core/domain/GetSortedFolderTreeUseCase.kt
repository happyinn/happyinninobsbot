package com.obsbot.happyinn.apps.happyinninobsbot.core.domain


import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.MediaRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Sort
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class GetSortedFolderTreeUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userDataRepository: UserDataRepository,
    @Dispatcher(HioDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(folderPath: String? = null): Flow<Folder?> = combine(
        mediaRepository.getFoldersFlow(),
        userDataRepository.userData,
    ) { folders, preferences ->
        val currentFolder = folderPath?.let {
            folders.find { it.path == folderPath } ?: return@combine null
        } ?: Folder.rootFolder

        val sort = Sort(by = preferences.sortBy, order = preferences.sortOrder)

        currentFolder.copy(
            folderList = folders.getFoldersFor(path = currentFolder.path, userData = preferences),
        ).let { folder ->
            if (folderPath == null) folder.getInitialFolderWithContent() else folder
        }.let { folder ->
            folder.copy(
                mediaList = folder.mediaList.sortedWith(sort.videoComparator()),
                folderList = folder.folderList.sortedWith(sort.folderComparator()),
            )
        }
    }.flowOn(defaultDispatcher)

    private fun Folder.getInitialFolderWithContent(): Folder {
        return when {
            mediaList.isEmpty() && folderList.size == 1 -> folderList.first().getInitialFolderWithContent()
            else -> this
        }
    }

    private fun List<Folder>.getFoldersFor(
        path: String,
        userData: UserData,
    ): List<Folder> {
        return filter {
            it.parentPath == path && it.path !in userData.excludeFolders
        }.map { directory ->
            Folder(
                name = directory.name,
                path = directory.path,
                dateModified = directory.dateModified,
                mediaList = directory.mediaList,
                folderList = getFoldersFor(path = directory.path, userData = userData),
            )
        }
    }
}
