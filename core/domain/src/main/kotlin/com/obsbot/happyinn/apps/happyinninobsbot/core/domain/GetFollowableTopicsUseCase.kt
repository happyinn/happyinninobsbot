package com.obsbot.happyinn.apps.happyinninobsbot.core.domain

import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.TopicsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.FollowableTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.sortedBy
import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.TopicSortField.NONE
import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.TopicSortField.NAME
import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.TopicSortField.entries


/**
 * A use case which obtains a list of topics with their followed state.
 */
class GetFollowableTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
    private val userDataRepository: UserDataRepository,
) {
    /**
     * Returns a list of topics with their associated followed state.
     *
     * @param sortBy - the field used to sort the topics. Default NONE = no sorting.
     */
    operator fun invoke(sortBy: TopicSortField = NONE): Flow<List<FollowableTopic>> = combine(
        userDataRepository.userData,
        topicsRepository.getTopics(),
    ) { userData, topics ->
        val followedTopics = topics
            .map { topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics,
                )
            }
        when (sortBy) {
            NAME -> followedTopics.sortedBy { it.topic.name }
            else -> followedTopics
        }
    }
}

enum class TopicSortField {
    NONE,
    NAME,
}
