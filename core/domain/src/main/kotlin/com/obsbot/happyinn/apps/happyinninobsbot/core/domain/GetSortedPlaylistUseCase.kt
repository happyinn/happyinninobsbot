package com.obsbot.happyinn.apps.happyinninobsbot.core.domain

import android.content.Context
import android.net.Uri
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaViewMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Video
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getPath
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GetSortedPlaylistUseCase @Inject constructor(
    private val getSortedVideosUseCase: GetSortedVideosUseCase,
    private val userDataRepository: UserDataRepository,
    @ApplicationContext private val context: Context,
    @Dispatcher(HioDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(uri: Uri): List<Video> = withContext(defaultDispatcher) {
        val path = context.getPath(uri) ?: return@withContext emptyList()
        val parent = File(path).parent.takeIf {
            userDataRepository.userData.first().mediaViewMode != MediaViewMode.VIDEOS
        }

        getSortedVideosUseCase.invoke(parent).first()
    }
}
